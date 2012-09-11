package test.kool.math

import io.kool.math.*
import kotlin.test.*
import org.junit.Test as test

class CalculationsOnBeansTest {

    class Trade(val symbol: String, val price: Double, val amount: Double) {
        fun toString() = "Trade($symbol, $price, $amount)"
    }

    test fun calculationsOnBeans() {
        val trades = arrayList(Trade("AAPL", 630.0, 100.0), Trade("GOOG", 625.0, 50.0), Trade("MSFT", 22.0, 5.0))

        val maxPrice = trades.max{ it.price }
        val amountVariance = trades.variance{ it.amount }
        val avgAmount = trades.mean{ it.amount }

        println("maxprice = $maxPrice, amountVariance = $amountVariance, avgAmount = $avgAmount")
        assertEquals(630.0, maxPrice)
        assertTrue(amountVariance > 0.0)
        assertTrue(avgAmount > 50.0)
    }
}