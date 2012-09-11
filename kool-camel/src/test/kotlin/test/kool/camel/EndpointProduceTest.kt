package test.kool.camel

import io.kool.camel.*
import kotlin.test.*
import org.apache.camel.*
import org.apache.camel.Processor as ExchangeProcessor
import org.junit.Test as test

class EndpointProduceTest {

    test fun endpointExchangeStream() {
        val context = createCamelContext()
        context.use {

            val inStream = context.endpoint("timer://foo?fixedRate=true&period=1000").toExchangeStream()

            val resultsEndpoint = context.endpoint("seda:resultsEndpoint")
            inStream.sendTo<Exchange>(resultsEndpoint)

            val outStream = resultsEndpoint.toStream()
            val cursor = outStream.take(4).open{ println("handler consuming from $resultsEndpoint got $it of type ${it.javaClass}") }

            Thread.sleep(6000)
            assertTrue(cursor.isClosed())
        }
    }


}