## Kool Templates

**Kool Templates** generate HTML5 markup as a DOM using a simple statically typed template language written in [Kotlin](http://jetbrains.github.com/kotlin/).

The DSL is generated from the [HTML5 specification](http://dev.w3.org/html5/spec/section-index.html) so that we can ensure that all the elements and attributes in the HTML specification are reflected in the [typesafe DSL](http://kool.io/versions/snapshot/apidocs/io/kool/template/html/org/w3c/dom/Node-extensions.html). You can still use strings for element and attribute names if you need to stray outside of the HTML5 specification; but for the cases where you want to stay inside HTML5 the DSL catches your typos.

Kool Templates are designed so that they can be used on the client side inside a web browser or used on the server side in a Servlet / JAXRS application. For example there is an example of using [Kool Templates in a web browser inside JavaFX](https://github.com/koolio/kool/tree/master/kool-javafx).

Using Kool Templates its easy to create an entire page or DOM fragment from any function or class.

However longer term we hope to allow active views to be created that automatically keep the DOM view updated as the underlying model changes  using [Kool Streams](https://github.com/koolio/kool/blob/master/kool-stream/ReadMe.md) to monitor your domain model or external events and update the correct parts of your view automatically.

### Examples

The easiest way to get started is to check out an example:

* [simple HTML5 example](https://github.com/koolio/kool/blob/master/kool-template/src/test/kotlin/test/kool/template/html/HtmlTemplateTest.kt#L12)
* [using a Kool Template in a browser with JavaFX](https://github.com/koolio/kool/tree/master/kool-javafx).

