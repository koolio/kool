package io.kool.sample.chat

import javax.ws.rs.*
import javax.ws.rs.core.Context

/**
 */
Path("/products/id2/{id}")
Produces("application/json")
public class ProductId {
    val products: Products = Products()

    PathParam("id") private var id: String? = null

    GET
    Produces("application/json")
    public fun get(): Product? {
        val answer = products.byId(id)
        println("Found $answer from $id")
        return answer
    }
}