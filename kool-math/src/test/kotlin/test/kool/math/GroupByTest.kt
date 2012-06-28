package test.kool.math

import io.kool.math.*
import io.kool.stream.*
import java.util.*

import org.junit.Test as test

class GroupByTest {

    class Trade(val symbol: String, val price: Double, val amount: Double) {
        fun toString() = "Trade($symbol, $price, $amount)"
    }

    test fun streamGroupBy() {
        val trades = arrayList(
                Trade("AAPL", 630.0, 100.0),
                Trade("GOOG", 626.0, 50.0),
                Trade("AAPL", 636.0, 150.0),
                Trade("GOOG", 625.0, 60.0),
                Trade("AAPL", 633.0, 100.0),
                Trade("AAPL", 635.0, 200.0),
                Trade("MSFT", 22.0, 5.0)
                )

        val stream = trades.toStream().window(4)
        val groupedStream = stream.groupBy{ (t: Trade) -> t.symbol }

        groupedStream.open { map ->
            for (e in map) {
                val symbol = e.key
                val trades = e.value

                val price = trades.mean{ it.price }
                val amount = trades.sum{ it.amount }
                println("$symbol has ${trades.size} trade(s) with mean price $price and total amount $amount")
            }

        }
        Thread.sleep(4000)
    }
}