package test.kool.math

import kotlin.test.*

import io.kool.math.*

import org.junit.Test as test
import java.util.List

class CalculationsTest {

    test fun calculationsOnDoubles() {
        val numbers = arrayList(1.0, 2.0, 3.0)
        val v = numbers.variance()

        println("variance of $numbers is $v")

        assertTrue(v > 0)
    }

    /**
    TODO compiler issue
    see: http://youtrack.jetbrains.com/issue/KT-1761

    test fun calculationsOnInts() {
        val numbers = arrayList(1, 2, 3)
        val v = numbers.variance()

        println("variance of $numbers is $v")

        assertTrue(v > 0)
    }
    */
}