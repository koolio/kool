package client

import js.Json

fun main(args: Array<String>) {
    println("Registering angularjs controller...")
    val myApp = angular.module("myApp", array())
    myApp.controller("ProductController",  {
        (`$scope`: Json) ->
            `$scope`["clear"] = {() ->
                println("Cleared!")
            }
            `$scope`["save"] = {() ->
                println("Saved!")
            }
    })
    println("Controller added!")
}
