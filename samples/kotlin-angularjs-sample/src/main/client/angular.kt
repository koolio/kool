import js.native
import js.library

/**
 * Generates the Kotlin API for AngularJS
 */

/**
 * Access to AngularJS
 */
native("angular") public var angular: Angular = null!!

native
trait Angular {
    fun module(name: String): AngularModule
}

native
trait AngularModule {
    fun controller(name: String, function: Any): Unit
}