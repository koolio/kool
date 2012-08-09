package io.kool.sample.chat

import javax.ws.rs.Produces

Produces("application/json")
public open class Product() {
    public var id: String? = null
    public var name: String? = null
    public var price: Double = 0.0

    public fun toString(): String = "Product($id, $name, $price)"

    class object {
        public open fun init(id: String, name: String, price: Double): Product {
            val answer = Product()
            answer.id = id
            answer.name = name
            answer.price = price
            return answer
        }
    }
}
