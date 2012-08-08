package io.kool.sample.chat

import java.util.Date

public open class Message() {
    public var text: String? = null
    public var author: String? = null
    public var time: Long = 0

    class object {
        public open fun init(author: String?, text: String?): Message {
            val answer = Message()
            answer.author = author
            answer.text = text
            answer.time = Date().getTime()
            return answer
        }
    }
}
