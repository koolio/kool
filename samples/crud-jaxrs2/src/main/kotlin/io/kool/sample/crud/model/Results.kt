package io.kool.sample.crud

import javax.ws.rs.Produces

Produces("application/json")
public open class Results<T>(val list: List<T>) {
}
