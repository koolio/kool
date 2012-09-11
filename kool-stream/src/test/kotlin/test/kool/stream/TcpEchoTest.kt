package test.kool.stream

import io.kool.stream.*
import java.io.*
import java.net.*
import java.nio.*
import java.nio.channels.*
import java.util.concurrent.CountDownLatch
import java.util.concurrent.Executors
import org.fusesource.hawtdispatch.*
import org.junit.Test as test

/**
* <p>
* </p>
*
* @author <a href="http://hiramchirino.com">Hiram Chirino</a>
*/
class TcpEchoTest {

    test fun testSimpleEchoServer() {
        excerciseEchoServer(TcpServer())
    }
    test fun testStreamEchoServer() {
        excerciseEchoServer(StreamTcpServer())
    }


    fun excerciseEchoServer(server:TcpServer) {
        val threadPool = Executors.newCachedThreadPool()!!

        try {
            println("Server started on port: "+ server.localPort())
            val socket = Socket("localhost", server.localPort())
            try {
                // write some data...

                // Start a fast writer thread..
                val data = "Hello World!".toByteArray("UTF-8")
                val data_count = (1024*1024*500)/data.size // lets send about 500 megs of data..

                threadPool.execute(runnable{
                    println("Transmitting socket data...")
                    val os = socket.getOutputStream()!!.buffered(1024*64)
                    for( i in 0.rangeTo(data_count)) {
                        os.write(data)
                    }
                    os.flush()
                    println("Transmision sent.")
                })

                val done = CountDownLatch(1)
                // lets read the data slowly..
                threadPool.execute(runnable{
                    println("Receiving socket data...")
                    var remaining = data.size * data_count
                    val input = socket.getInputStream()!!
                    while(remaining > 0) {
                        var r = Math.min(remaining, 1024*1024*100)
                        while( r > 0 ) {
                            val c = input.skip(r.toLong())
                            remaining -= c
                            r -= c
                        }
                        Thread.sleep(500)
                    }
                    done.countDown()
                    println("Transmision recieved.")
                })
                done.await()

            } finally {
                socket.close()
            }

        } finally {
            server.close()
        }
    }

}

fun main(args:Array<String>):Unit {
    TcpEchoTest().testSimpleEchoServer()
    TcpEchoTest().testStreamEchoServer()
}

class StreamTcpConnection(socket : SocketChannel) : TcpConnection(socket) {

    val stream = TcpStream()
    class TcpStream : Stream<ByteArray>() {
        override public fun open(handler : Handler<ByteArray>) : Cursor {
            throw UnsupportedOperationException()
        }

        override public fun open(handler: NonBlockingHandler<ByteArray>): NonBlockingCursor {
            if( cursor.handler!=null )
                throw IllegalStateException("Handler allready open.")
            cursor.handler = handler
            handler.onOpen(cursor)
            resumeReads()
            return cursor
        }
    }

    // this gets called when data arrives on the socket... send it to the cursor's handler..
    // if this returns false, the TCP socket not read again until the cursor is resumed.
    override fun offerRead(data : ByteArray) : Boolean {
        val h = cursor.handler
        if( h!=null ) {
            return h.offerNext(data)
        } else {
            return false
        }
    }

    val cursor = TcpCursor()
    class TcpCursor : NonBlockingCursor {
        var handler : NonBlockingHandler<ByteArray>? = null

        // the handler will call resume when it can accept more read data..
        public override fun wakeup() {
            // re-enable reads from the socket.
            resumeReads()
        }
        public override fun isClosed() : Boolean {
            return handler==null
        }
        public override fun close() {
            handler = null
        }
    }


    // A handler which writes data it receives to the socket.
    val handler = TcpHandler()
    class TcpHandler : NonBlockingHandler<ByteArray>() {
        var cursor: NonBlockingCursor? = null
        public override fun onOpen(cursor : NonBlockingCursor) {
            if( this.cursor!=null )
                throw IllegalStateException("Handler allready open.")
            this.cursor = cursor
        }

        public override fun offerNext(next : ByteArray) = offerWrite(next)

        public override fun onComplete() {
            this.cursor = null
        }
        public override fun onError(e : Throwable) = onComplete()
    }

    // This gets called back once to tcp socket can accept more writes.
    override fun onWriteRefill() : Unit {
        val c = handler.cursor
        if(c!=null) {
            c.wakeup()
        }
    }

}

class StreamTcpServer : TcpServer() {

    override fun createConnection(socket : SocketChannel):TcpConnection {
        val connection = StreamTcpConnection(socket)
        // just have read events out out to the handler..
        connection.stream.open( connection.handler )
        return connection
    }
}


open class TcpServer(val port : Int = 0) {

    val queue = Dispatch.createQueue()!!
    val channel = ServerSocketChannel.open()!!
    val acceptEvents = Dispatch.createSource(channel, SelectionKey.OP_ACCEPT, queue)!!;

    {
        channel.socket()!!.bind(InetSocketAddress(port))
        channel.configureBlocking(false)
        acceptEvents.setCancelHandler(runnable{
            println("Closed server socket on port ${localPort()}")
            channel.close()
        })
        acceptEvents.setEventHandler(runnable{

            // Keep accepting until we run out of sockets to open..
            while( true ) {
                val socket = channel.accept()
                if( socket != null ) {
                    try {
                        socket.configureBlocking(false)
                        createConnection(socket).resume()
                    } catch (e : Exception) {
                        socket.close()
                    }
                } else {
                    break;
                }
            }
        })
        acceptEvents.resume()
    }

    // Allow subclasses to use a use a custom subclassed TcpConnection Class.
    open fun createConnection(socket:SocketChannel) = TcpConnection(socket)

    fun localPort() = channel.socket()!!.getLocalPort()

    fun close():Unit {
        acceptEvents.cancel()
    }
}

open class TcpConnection(val channel : SocketChannel) {

    val queue = Dispatch.createQueue()!!
    val buffer = ByteBuffer.allocate(1024)!!
    val readEvents = Dispatch.createSource(channel, SelectionKey.OP_READ, queue)!!
    val writeEvents = Dispatch.createSource(channel, SelectionKey.OP_WRITE, queue)!!

    ;
    {
        readEvents.setCancelHandler(runnable {
            writeEvents.cancel()
        })
        writeEvents.setCancelHandler(runnable{
            println("Closed connection from: ${remoteAddress()}")
            channel.close()
        })
        readEvents.setEventHandler(runnable{ processReads()  })
        writeEvents.setEventHandler(runnable{ processWrites() })
        println("Accepted connection from: ${remoteAddress()}")
    }

    fun resume() = readEvents.resume()
    fun suspend() = readEvents.suspend()
    fun remoteAddress() = channel.socket()?.getRemoteSocketAddress().toString()?:"n/a"
    fun close() = readEvents.cancel()

    var pendingRead :ByteArray? = null
    var lastReadOfferRejected = false

    fun processReads():Unit {
        queue.assertExecuting()
        try {
            while(true) {
                if( pendingRead !=null ) {
                    if( offerRead(pendingRead!!) ) {
                        pendingRead = null
                    } else {
                        // the pendingRead was not accepted.. so suspend from doing further reads.
                        if( !lastReadOfferRejected ) {
                            lastReadOfferRejected = true
                            readEvents.suspend();
                            println("read suspended...")
                        }
                        return;
                    }
                } else {
                    if (channel.read(buffer) == - 1) {
                        close()
                        return;
                    } else {
                        buffer.flip()
                        if (buffer.remaining() > 0) {
                            pendingRead = buffer.array()!!.copyOf(buffer.remaining())
                        }
                        buffer.clear()
                    }
                }
            }
        } catch(e : IOException) {
            close()
        }
    }

    open fun offerRead(data:ByteArray):Boolean = offerWrite(data)
    open fun onWriteRefill() = resumeReads()

    fun resumeReads() = queue.execute(runnable{
        if( lastReadOfferRejected ) {
            lastReadOfferRejected = false
            readEvents.resume();
            println("read resumed...")
            processReads();
        }
    })

    var pendingWrite :ByteBuffer? = null
    var lastWriteOfferRejected = false

    fun offerWrite(data:ByteArray):Boolean {
        queue.assertExecuting()
        if( pendingWrite==null ) {
            pendingWrite = ByteBuffer.wrap(data)
            processWrites()
            return true
        } else {
            if( !lastWriteOfferRejected ) {
                lastWriteOfferRejected = true
            }
            return false
        }
    }


    fun processWrites():Unit {
        queue.assertExecuting()
        try {
            while( true ) {
                if (pendingWrite == null) {
                    if( !writeEvents.isSuspended() ) {
                        println("Write buffers have been drained..")
                        writeEvents.suspend()
                    }
                    if( lastWriteOfferRejected ) {
                        lastWriteOfferRejected = false
                        onWriteRefill()
                    }
                    return;
                } else {
                    if (pendingWrite?.remaining() == 0) {
                        pendingWrite = null
                    } else {
                        val count = channel.write(pendingWrite)
                        if( count == 0 ) {
                            if( writeEvents.isSuspended() ) {
                                writeEvents.resume()
                                println("waiting for write buffers to drain...")
                            }
                            return;
                        } else {
                            // println("Sent: ${count} bytes of data")
                        }
                    }
                }
            }
        } catch(e : IOException) {
            close()
        }
    }

}