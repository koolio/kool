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
public open class Jaxrs2Chat() {
    protected val responses: List<Response> = ArrayList<Response>()

    Context var ctx: ExecutionContext? = null

    Suspend GET
    public open fun suspend(): String? {
        return ""
    }

    GET
    Path("responses")
    Produces("application/json")
    public open fun getResponses(): Responses {
        return Responses.init(responses)
    }

    POST
    Broadcast(writeEntity = false)
    public open fun broadcast(message: Message): Response? {
        val answer = Response.init(message.author, message.message)
        responses.add(answer)
        return answer
    }


}
