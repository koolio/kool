package io.kool.sample.chat

public open class Message() {
    public var author: String? = ""
    public var message: String? = ""

    class object {
        public open fun init(author: String?, message: String?): Message {
            val answer = Message()
            answer.author = author
            answer.message = message
            return answer
        }
    }
}
