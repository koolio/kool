package test.kool.camel

import io.kool.camel.*
import org.junit.Test as test

class FilterTest {
    test fun createRoute() {
        camel {
            val result = mockEndpoint("mock:result")
            routes {
                from("seda:foo") {
                    filter({ bodyString().contains("big") }, {
                        sendTo(result)
                    })
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
