package io.kool.website

import javax.servlet.annotation.WebListener
import io.kool.web.ContextListener
import javax.servlet.ServletContext
import io.kool.web.LayoutServletFilter

[WebListener]
public class MyContextListener() : ContextListener() {

    public override fun createLayoutFilter(sc: ServletContext): LayoutServletFilter? {
        return MyLayoutFilter()
    }
}
