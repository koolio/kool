package test.kool.template.html.bind

import java.util.ArrayList

class Person(var name: String, var orders: MutableList<Order> = ArrayList<Order>()) {
}

class Order(val amount: Int, val productName: String) {
}