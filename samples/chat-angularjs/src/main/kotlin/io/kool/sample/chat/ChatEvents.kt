package io.kool.sample.chat

import com.sun.jersey.spi.resource.Singleton
import java.util.ArrayList
import java.util.List
import javax.ws.rs.*
import javax.ws.rs.core.Context
import javax.ws.rs.core.ExecutionContext
import org.atmosphere.annotation.Broadcast

Produces("application/json")
Singleton
public open class ChatEvents(var room: ChatRoom) {
    Context var ctx: ExecutionContext? = null

    Suspend GET
    public open fun suspend(): String? {
        return ""
    }

    POST
    Broadcast(writeEntity = false)
    public open fun broadcast(message: NewMessage): Message? {
        return room.send(message)
    }
}
