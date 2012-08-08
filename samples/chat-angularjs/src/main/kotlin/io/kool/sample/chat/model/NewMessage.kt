package io.kool.sample.chat

public open class NewMessage() {
    public var author: String? = ""
    public var text: String? = ""

    public fun toString(): String = "NewMessage(@$author: $text)"

    class object {
        public open fun init(author: String?, message: String?): NewMessage {
            val answer = NewMessage()
            answer.author = author
            answer.text = message
            return answer
        }
    }
}
