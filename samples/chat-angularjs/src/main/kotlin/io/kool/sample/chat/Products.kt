package io.kool.sample.chat

import com.sun.jersey.spi.resource.Singleton
import java.util.ArrayList
import java.util.List
import javax.ws.rs.*
import org.atmosphere.annotation.Broadcast
import javax.ws.rs.core.Context
import javax.ws.rs.core.ExecutionContext
import java.util.UUID
import java.util.concurrent.atomic.AtomicInteger

Path("/products")
Produces("application/json")
Singleton
public open class Products() {
    private val idGenerator = AtomicInteger(0)
    protected val collection: List<Product> = arrayList(Product.init(nextId, "Beer", 3.99), Product.init(nextId, "Wine", 5.99))

    public val nextId: String
    get() = idGenerator.incrementAndGet().toString()

    GET
    public open fun results(): Results<Product> = Results<Product>(collection)

    GET
    Path("{id}")
    public open fun get(id: String): Product? {
        return collection.find { it.id == id }
    }

    DELETE
    Path("{id}")
    public open fun remove(id: String): Product? {
        val element = get(id)
        if (element != null) {
            collection.remove(element)
        }
        return element
    }

    POST
    public open fun add(element: Product): Product {
        val id = element.id
        if (id != null) {
            remove(id)
        } else {
            element.id = nextId
        }
        println("add $element")
        collection.add(element)
        return element
    }

}
