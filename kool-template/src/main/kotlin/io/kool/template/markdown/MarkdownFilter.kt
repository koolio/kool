package io.kool.template.markdown

import io.kool.template.*

import org.pegdown.PegDownProcessor
import org.pegdown.Extensions
import org.pegdown.LinkRenderer

/**
 * Converts markdown into HTML
 */
class MarkdownFilter : TextFilter {
    public var markdownProcessor: PegDownProcessor = PegDownProcessor(Extensions.ALL)
    public var linkRendered: LinkRenderer = LinkRenderer()

    fun toString() = "MarkdownFilter"

    override fun filter(filterContext: FilterContext, appendable: Appendable) {
        val text = filterContext.source.text()
        val output = markdownProcessor.markdownToHtml(text, linkRendered)
        if (output != null) {
            filterContext.outputContentType = "text/html"
            appendable.append(output)
        }
    }

    override fun getUrlMapping(): Array<String> {
        return array("*.md")
    }
}