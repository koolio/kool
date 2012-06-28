import kotlin.modules.*

fun project() {
    module("website") {
        addSourceFiles("../kool-stream/src/main/kotlin")
        //addSourceFiles("../kool-template/src/main/kotlin")
    }
}