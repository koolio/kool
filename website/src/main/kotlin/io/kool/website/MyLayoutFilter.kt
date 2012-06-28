package io.kool.website

import io.kool.template.FilterContext
import io.kool.template.Template
import io.kool.web.*

// TODO
//[WebFilter(displayName = "LayoutFilter", urlPatterns = array("*", "*.html"))]
class MyLayoutFilter(): LayoutServletFilter() {

    public override fun findLayoutTemplate(context: FilterContext): Template? {
        val contentType = context.outputContentType
        println("content type: $contentType")
        if (contentType != null && contentType.startsWith("text/html")) {
            println("Matching for request: ${context.requestContext.uri}")
            return DefaultLayoutTemplate(context.source.text())
        } else {
            return null
        }
    }
}