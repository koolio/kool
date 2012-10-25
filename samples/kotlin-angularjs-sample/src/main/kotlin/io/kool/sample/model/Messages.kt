package io.kool.sample.model

import java.util.ArrayList

public open class Messages() {
    public var messages: List<Message> = ArrayList<Message>()
}

// TODO would be nice to just use a constructor here when we can generate zero arg bytecode ctor too ;)
public fun messages(messages: List<Message>): Messages {
    val answer = Messages()
    answer.messages = messages
    return answer
}