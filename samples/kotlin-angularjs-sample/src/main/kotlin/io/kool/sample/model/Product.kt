package io.kool.sample.model

//Produces("application/json")
public open class Product() {
    public var id: String? = null
    public var name: String? = null
    public var price: Double = 0.0

    public fun toString(): String = "Product($id, $name, $price)"
}

// TODO would be nice to just use a constructor here when we can generate zero arg bytecode ctor too ;)
public fun product(id: String, name: String, price: Double = 0.0): Product {
    val answer = Product()
    answer.id = id
    answer.name = name
    answer.price = price
    return answer
}
