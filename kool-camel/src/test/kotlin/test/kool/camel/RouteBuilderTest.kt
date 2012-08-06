package test.kool.camel

import io.kool.camel.*

import org.junit.Test as test
import org.apache.camel.component.mock.MockEndpoint

class RouteBuilderTest {
    test fun createRoute() {
        val context = createCamelContext()

        context.use {
            val result = mockEndpoint("mock:result")
            routes {
                from("seda:foo") {
                    sendTo(result)
                }
            }
            println("Now has routes ${context}")

            val body1 = "<hello>world!</hello>"
            result.expectedBodiesReceived(body1)

            val producer = producerTemplate()
            producer.sendBody("seda:foo", body1)

            result.assertIsSatisfied()

            for (exchange in result.getReceivedExchanges()) {
                println("Found message ${exchange?.input}")
            }
        }
    }
}
