package io.kool.template

/**
 */
trait TextFilter {
    /**
     * Processes the source and generates the output to the given output
     */
    fun filter(filterContext: FilterContext, appendable: Appendable): Unit

    fun getUrlMapping(): Array<String>
}