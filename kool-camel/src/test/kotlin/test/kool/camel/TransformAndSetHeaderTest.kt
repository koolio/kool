package test.kool.camel

import io.kool.camel.*
import org.junit.Test as test

class TransformAndSetHeaderTest {
    test fun createRoute() {
        camel {
            val result = mockEndpoint("mock:result")
            routes {
                from("seda:foo") {
                    transform {
                        out["foo"] = input["breadCrumbId"]
                        out["bar"] = 123
                        "Hello ${bodyString()}"
                    }.sendTo(result)
                }
            }
            result.expectedBodiesReceived("Hello world!")
            result.expectedHeaderReceived("bar", 123)

            val producer = producerTemplate()
            producer.sendBody("seda:foo", "world!")

            result.assertIsSatisfied()

            for (exchange in result.getReceivedExchanges()) {
                println("Found message ${exchange?.input} with headers ${exchange?.input?.headers}")
            }
        }
    }
}
