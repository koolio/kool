package io.kool.template

import kotlin.util. *
import java.util.ServiceLoader
import java.util.List


/**
 * Loads all the filters from the given class loader if specified
 */
fun loadTextFilters(classLoader: ClassLoader? = null): List<TextFilter> {
    val filterClass = javaClass<TextFilter>()
    val loader = if (classLoader != null) {
        ServiceLoader.load(filterClass, classLoader)
    } else {
        ServiceLoader.load(filterClass)
    }
    val answer = arrayList<TextFilter>()
    if (loader != null) {
        for (s in loader.iterator()) {
            answer.add(s)
        }
    }
    return answer
}