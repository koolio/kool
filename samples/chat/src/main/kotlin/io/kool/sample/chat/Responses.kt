package io.kool.sample.chat

import java.util.ArrayList
import java.util.List

public open class Responses() {
    public var responses: List<Response> = ArrayList<Response>()

    class object {
        public open fun init(responses: List<Response>): Responses {
            val answer = Responses()
            answer.responses = responses
            return answer
        }
    }
}
