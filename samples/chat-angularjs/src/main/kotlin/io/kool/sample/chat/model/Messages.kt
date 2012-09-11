package io.kool.sample.chat

import java.util.ArrayList

public open class Messages() {
    public var messages: List<Message> = ArrayList<Message>()

    class object {
        public open fun init(messages: List<Message>): Messages {
            val answer = Messages()
            answer.messages = messages
            return answer
        }
    }
}
