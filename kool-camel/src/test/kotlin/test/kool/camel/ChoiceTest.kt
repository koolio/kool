package test.kool.camel

import io.kool.camel.*

import org.junit.Test as test
import org.apache.camel.component.mock.MockEndpoint

class ChoiceTest {
    test fun createRoute() {
        camel {
            val bigEndpoint = mockEndpoint("mock:big")
            val smallEndpoint = mockEndpoint("mock:small")
            val otherwiseEndpoint = mockEndpoint("mock:otherwise")

            routes {
                from("seda:foo") {
                    choice {
                        filter { bodyString().contains("big") } then {
                            sendTo(bigEndpoint)
                        }
                        filter { bodyString().contains("small") } then {
                            sendTo(smallEndpoint)
                        }
                        otherwise {
                            sendTo(otherwiseEndpoint)
                        }
                    }
                }
            }
            bigEndpoint.expectedBodiesReceived("big1")
            smallEndpoint.expectedBodiesReceived("small1", "small2")
            otherwiseEndpoint.expectedBodiesReceived("dummy1", "dummy2")

            val producer = producerTemplate()
            for (body in arrayList("dummy1", "small1", "big1", "small2", "dummy2")) {
                producer.sendBody("seda:foo", body)
            }

            for (result in arrayList(bigEndpoint, smallEndpoint, otherwiseEndpoint)) {
                result.assertIsSatisfied()
                println("$result has messages:")
                for (exchange in result.getReceivedExchanges()) {
                    println("    ${exchange?.input}")
                }
                println()
            }
        }
    }
}
