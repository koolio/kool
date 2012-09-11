package io.kool.sample.chat

import java.util.ArrayList

import javax.ws.rs.Produces

Produces("application/json")
public open class Results<T>(val list: List<T>) {
}
