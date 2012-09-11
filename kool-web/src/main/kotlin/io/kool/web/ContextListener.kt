package io.kool.web

import io.kool.template.*

import javax.servlet.annotation.WebListener
import javax.servlet.ServletContextListener
import javax.servlet.ServletContextEvent

import javax.servlet.ServletContext

/**
 * Base class for creating your own context listener
 */
abstract class ContextListener : ServletContextListener {

    public override fun contextInitialized(event: ServletContextEvent?) {
        if (event != null) {
            val sc = event.getServletContext()
            if (sc != null) {
                sc.log("Stating the kool.io context: $this")
                for (filter in loadContextFilterRendererServlets(sc)) {
                    val name = filter.toString()

                    val servlet = TextFilterServlet(filter)
                    val registration = sc.addServlet(name, servlet)
                    if (registration != null) {
                        val mappings = filter.getUrlMapping()
                        sc.log("Adding filter: $filter with mappings: ${mappings.toList()}")
                        for (mapping in mappings) {
                            registration.addMapping(mapping)
                        }
                    }
                }
                val layout = createLayoutFilter(sc)
                if (layout != null) {
                    val registration = sc.addFilter("LayoutFilter", layout)
                    // TODO cannot invoke varargs....
                    if (registration != null) {
                        for (mapping in layout.urlMapping) {
                            registration.addMappingForUrlPatterns(null, true, mapping)
                        }
                    }
                }
            }
        }
    }

    public override fun contextDestroyed(event: ServletContextEvent?) {
    }

    open protected fun createLayoutFilter(sc: ServletContext): LayoutServletFilter? {
        return null
    }

    open protected fun loadContextFilterRendererServlets(sc: ServletContext): List<TextFilter> {
       return loadTextFilters(Thread.currentThread().sure().getContextClassLoader())
       //return loadTextFilters(sc.getClassLoader())
    }
}
