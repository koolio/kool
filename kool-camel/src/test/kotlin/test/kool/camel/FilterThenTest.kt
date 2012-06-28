package test.kool.camel

import io.kool.camel.*

import org.junit.Test as test
import org.apache.camel.component.mock.MockEndpoint

class FilterThenTest {
    test fun createRoute() {
        camel {
            val result = mockEndpoint("mock:result")
            routes {
                from("seda:foo") {
                    filter { bodyString().contains("big") } then {
                        sendTo(result)
                    }
                }
            }

            result.expectedBodiesReceived("big1")

            val producer = producerTemplate()
            for (body in arrayList("small1", "big1", "small2")) {
                producer.sendBody("seda:foo", body)
            }

            result.assertIsSatisfied()

            for (exchange in result.getReceivedExchanges()) {
                println("Found message ${exchange?.input}")
            }
        }
    }
}
