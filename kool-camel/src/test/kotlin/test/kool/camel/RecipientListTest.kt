package test.kool.camel

import org.junit.Test as test
import io.kool.camel.*

class RecipientListTest {
    test fun createRoute() {
        camel {
            routes {
                from("seda:foo") {
                    recipientList(",") { input["recipientListHeader"] }
                }
            }

            val resultEndpoints = arrayList(mockEndpoint("mock:x"), mockEndpoint("mock:y"), mockEndpoint("mock:z"))
            val body = "answer"
            for (result in resultEndpoints) {
                result.expectedBodiesReceived(body)
            }

            val producer = producerTemplate()
            producer.sendBodyAndHeader("seda:foo", body,
                    "recipientListHeader", "mock:x,mock:y,mock:z")

            for (result in resultEndpoints) {
                result.assertIsSatisfied()
                for (exchange in result.getReceivedExchanges()) {
                    println("$result has messages:")
                    for (exchange in result.getReceivedExchanges()) {
                        println("    ${exchange?.input}")
                    }
                    println()
                }
            }
        }
    }
}
