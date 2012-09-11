package io.kool.website

import io.kool.web.ContextListener
import io.kool.web.LayoutServletFilter
import javax.servlet.ServletContext
import javax.servlet.annotation.WebListener

[WebListener]
public class MyContextListener() : ContextListener() {

    public override fun createLayoutFilter(sc: ServletContext): LayoutServletFilter? {
        return MyLayoutFilter()
    }
}
