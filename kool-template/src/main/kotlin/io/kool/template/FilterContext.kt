package io.kool.template

/**
 * Represents the context of a filter request
 */
class FilterContext(val requestContext: RequestContext, val source: Input) {

    /**
     * Returns the MIME content type output
     */
    public var outputContentType: String? = null
}