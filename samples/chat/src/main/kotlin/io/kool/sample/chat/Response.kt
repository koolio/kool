package io.kool.sample.chat

import java.util.Date

public open class Response() {
    public var text: String? = null
    public var author: String? = null
    public var time: Long = 0

    class object {
        public open fun init(author: String?, text: String?): Response {
            val answer = Response()
            answer.author = author
            answer.text = text
            answer.time = Date().getTime()
            return answer
        }
    }
}
