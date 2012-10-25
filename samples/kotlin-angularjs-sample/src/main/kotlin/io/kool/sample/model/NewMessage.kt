package io.kool.sample.model

public open class NewMessage() {
    public var author: String? = ""
    public var text: String? = ""

    public fun toString(): String = "NewMessage(@$author: $text)"
}

// TODO would be nice to just use a constructor here when we can generate zero arg bytecode ctor too ;)
public fun newMessage(author: String?, message: String? = ""): NewMessage {
    val answer = NewMessage()
    answer.author = author
    answer.text = message
    return answer
}
