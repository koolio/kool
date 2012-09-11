package test.kool.camel

import io.kool.camel.*
import kotlin.test.*
import org.apache.camel.Processor as ExchangeProcessor
import org.junit.Test as test

class EndpointConsumeTest {

    test fun endpointExchangeStream() {
        val context = createCamelContext()
        context.use {
            val stream = context.endpoint("timer://foo?fixedRate=true&period=1000").toExchangeStream()
            val cursor = stream.take(4).open{ println("Stream Exchange handler got $it of type ${it.javaClass} with properties ${it.getProperties()}") }

            Thread.sleep(6000)
            assertTrue(cursor.isClosed())
        }
    }

    test fun endpointStream() {
        val context = createCamelContext()
        context.use {
            val stream = context.endpoint("timer://foo?fixedRate=true&period=1000").toStream()
            val cursor = stream.take(4).open{ println("Stream Message handler got $it of type ${it.javaClass} with headers ${it.getHeaders()}") }

            Thread.sleep(6000)
            assertTrue(cursor.isClosed())
        }
    }

    test fun endpointStreamMapToHeader() {
        val context = createCamelContext()
        context.use {
            val stream = context.endpoint("timer://foo?fixedRate=true&period=1000").toStream()
            val cursor = stream.map{ it.getHeader<String>("firedTime", javaClass<String>()) }.take(4).open{
                println("Stream String handler got $it of type ${it.javaClass}")
            }

            Thread.sleep(6000)
            assertTrue(cursor.isClosed())
        }
    }

    test fun endpointHeaderStreamByFunction() {
        val context = createCamelContext()
        context.use {
            val stream = context.endpoint("timer://foo?fixedRate=true&period=1000").toStream {
                it.getIn()?.getHeader<String>("firedTime", javaClass<String>())
            }
            val cursor = stream.take(4).open{
                println("Stream String handler got $it of type ${it.javaClass}")
            }

            Thread.sleep(6000)
            assertTrue(cursor.isClosed())
        }
    }


}