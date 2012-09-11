package test.kool.math

import io.kool.math.*
import io.kool.stream.*
import java.util.*
import org.junit.Test as test

/*
* based on this Esper sample http://esper.codehaus.org/esper-4.5.0/doc/reference/en/html/examples.html#examples-marketdata-monitor
*/
class MarketDataMonitorTest {

    class Trade(val symbol: String, val price: Double, val amount: Double) {
        fun toString() = "Trade($symbol, $price, $amount)"
    }

    class FallOff(val symbol: String, val count: Double, val averageCount: Double) {
        fun toString() = "FallOff($symbol, $count, $averageCount)"
    }

    /**
     * Lets transpose a list of maps of values into a map of list of values
     */
    fun <K,V> transposeListOfMapsToMapOfLists(listOfMaps: List<Map<K,V>>): Map<K,MutableList<V>> {
        val answer = HashMap<K,MutableList<V>>()
        for (map in listOfMaps) {
            for (e in map) {
                val list = answer.getOrPut<K,MutableList<V>>(e.key) {ArrayList<V>()}
                list.add(e.value)
            }
        }
        return answer
    }

     /**
     * Lets convert a list of maps of values to a list of FallOff values
     */
    fun toFallOffs(listOfMaps: List<Map<String,Double>>): List<FallOff> {
        val mapOfLists = transposeListOfMapsToMapOfLists<String,Double>(listOfMaps)
        return mapOfLists.map{ (e: Map.Entry<String,List<Double>>) ->
            val symbol = e.getKey()
            val counts = e.getValue()
            FallOff(symbol, counts.sum(), counts.geometricMean())
        }
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

        val tradeStream = SimpleStream<Trade>()
        val ticksBySymbol: Stream<Map<String,List<Trade>>> = tradeStream.timeWindow(1000).groupBy{ (t: Trade) -> t.symbol }
        val ticksPerSecondStream: Stream<Map<String,Double>> = ticksBySymbol.map<Map<String,Double>> {
            it.mapValues<String,List<Trade>,Double> { (e: Map.Entry<String,List<Trade>>) -> e.getValue().size().toDouble() }
        }

        val timeWindow: Stream<List<Map<String,Double>>> = ticksPerSecondStream.timeWindow(10000)
        val fallOffStream: Stream<List<FallOff>> = timeWindow.map { toFallOffs(it) }

        // TODO
        //val alertStream = fallOffStream.flatMap{ list -> list.filter{ it.count < it.averageCount * 0.75 } }

        fallOffStream.open {
            //val warnings = it.filter{ it.count < it.averageCount * 0.75 }
            val warnings = it
            for (f in warnings) {
                println("Fall off $it")
            }
        }

        // now lets fire some events
        var delay: Long = 800
        for (i in 0.rangeTo(2)) {
            for (t in trades) {
                tradeStream.onNext(t)
                Thread.sleep(delay)
                delay += 50
            }
        }
        Thread.sleep(1000)
    }
}