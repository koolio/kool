package io.kool.sample.chat

import com.sun.jersey.spi.resource.Singleton
import java.util.ArrayList
import java.util.List
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.ExecutionContext
import org.atmosphere.annotation.Broadcast

Path("/")
Produces("application/json")
Singleton
public open class ChatRoom() {
    protected val messages: List<Message> = ArrayList<Message>()

    Context var ctx: ExecutionContext? = null

    Suspend GET
    public open fun suspend(): String? {
        return ""
    }

    GET
    Path("messages")
    public open fun getMessages(): Messages {
        return Messages.init(messages)
    }

    POST
    Broadcast(writeEntity = false)
    public open fun broadcast(message: NewMessage): Message? {
        println("sending message $message")
        val answer = Message.init(message.author, message.text)
        messages.add(answer)
        return answer
    }
}
