package io.kool.sample.resources

import com.sun.jersey.spi.resource.Singleton
import java.util.ArrayList
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.ExecutionContext
import org.atmosphere.annotation.Broadcast
import io.kool.sample.model.*

Path("/chat")
Produces("application/json")
Singleton
public open class ChatRoom() {
    protected val messages: MutableList<Message> = ArrayList<Message>()
    Context var ctx: ExecutionContext? = null

    GET
    public open fun getMessages(): Messages {
        return messages(messages)
    }

    Path("events")
    Suspend GET
    public open fun suspend(): String? {
        return ""
    }

    POST
    public open fun send(message: NewMessage): Message? {
        println("sending message $message")
        val answer = message(message.author, message.text)
        messages.add(answer)
        return answer
    }

    POST
    Path("events")
    Broadcast(writeEntity = false)
    public open fun broadcast(message: NewMessage): Message? {
        return send(message)
    }
}
