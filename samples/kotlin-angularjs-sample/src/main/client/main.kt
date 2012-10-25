package client

fun main(args: Array<String>) {
    println("Hello world!")

    angular.module("Foo").controller("ProductController",  {
        (`$scope`: Any) -> ProductController(`$scope`)
    } )

    println("Added controller!")
}

class ProductController(val scope: Any) {
    fun clear() {
        println("Cleared!")
    }

    fun save() {
        println("Saved!")
    }
}