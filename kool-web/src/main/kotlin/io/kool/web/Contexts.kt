package io.kool.web

import io.kool.template.*
import javax.servlet.http.*

fun filterContext(request: HttpServletRequest, response: HttpServletResponse, source: Input): FilterContext? {
    val path = request.getServletPath()
    if (path != null) {
        val requestContext = HttpRequestContext(path, request, response)
        return FilterContext(requestContext, source)
    }
    return null
}
