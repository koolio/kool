package io.kool.sample.model

import java.util.Date

public open class Message() {
    public var text: String? = null
    public var author: String? = null
    public var time: Long = 0
}

// TODO would be nice to just use a constructor here when we can generate zero arg bytecode ctor too ;)
public fun message(author: String?, text: String? = null): Message {
    val answer = Message()
    answer.author = author
    answer.text = text
    // TODO doesn't compile in JS...
    //answer.time = Date().getTime()
    return answer
}
