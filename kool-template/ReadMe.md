## Kool Templates

**Kool Templates** generate HTML5 markup as a DOM using a simple statically typed template language written in [Kotlin](http://jetbrains.github.com/kotlin/).

With Kool Templates its easy to create an entire page or DOM fragment from any function or class.

The DSL is generated from the [HTML5 specification](http://dev.w3.org/html5/spec/section-index.html) so that we can ensure that all the elements and attributes in the HTML specification are reflected in the [typesafe DSL](http://kool.io/versions/snapshot/apidocs/io/kool/template/html/org/w3c/dom/Node-extensions.html). You can still use strings for element and attribute names if you need to stray outside of the HTML5 specification; but for the cases where you want to stay inside HTML5 the DSL catches your typos.

Kool Templates are designed so that they can be used on the client side inside a web browser or used on the server side in a Servlet / JAXRS application. For example there is an example of using [Kool Templates in a web browser or in a JVM with JavaFX](https://github.com/koolio/kool/tree/master/samples/kool-template-sample).


### Updating the DOM and event binding

Since Kool Templates are based on the DOM API, you can bind directly to DOM events from inside the template DSL.
For example see the use of the [onClick() function on the button in this example](https://github.com/koolio/kool/blob/master/samples/kool-template-bind-sample/src/main/kotlin/test/kool/myapp/MyApp.kt#L35))

Also using a **Binder** to bind expressions to nodes, its easy to allow in-place updates of the DOM using the [bind function](https://github.com/koolio/kool/blob/master/samples/kool-template-bind-sample/src/main/kotlin/test/kool/myapp/MyApp.kt#L28) so that you can easily [refresh](https://github.com/koolio/kool/blob/master/samples/kool-template-bind-sample/src/main/kotlin/test/kool/myapp/MyApp.kt#L45) just the parts of the DOM you need when events occur, such as a [button click](https://github.com/koolio/kool/blob/master/samples/kool-template-bind-sample/src/main/kotlin/test/kool/myapp/MyApp.kt#L35).

### Examples

The easiest way to get started is to check out an example:

* [simple HTML5 template example](https://github.com/koolio/kool/blob/master/kool-template/src/test/kotlin/test/kool/template/html/HtmlTemplateTest.kt#L12)
* [using a Kool Template in a web browser or in JavaFX](https://github.com/koolio/kool/tree/master/samples/kool-template-sample).
* [Kool Template and binding to DOM events](https://github.com/koolio/kool/tree/master/samples/kool-template-bind-sample) which handles button clicks etc.
* [using kotlin, Kool Templates and vert.x together](https://github.com/alextkachman/vertex-kotlin/blob/master/test/org/vertx/kotlin/examples/koolio/sample.kt#L14) with  [instructions for running the sample](https://github.com/alextkachman/vertex-kotlin/blob/master/ReadMe.md). Its also worth checking out [vert.x](http://vertx.io/) and the [vert.x kotlin support](https://github.com/alextkachman/vertex-kotlin)

### Converting HTML into Kool Templates

There are helper functions and an executable main() in the [io.kool.html.tokool](https://github.com/koolio/kool/blob/master/kool-html-parse/src/main/kotlin/io/kool/html/tokool/HtmlToKool.kt#L12) package which generates Kotlin source code using Kool Templates from a HTML document. So if you have any HTML you wish to generate you can easily turn it into a Kool Template!

See [this test case](https://github.com/koolio/kool/blob/master/kool-html-parse/src/test/kotlin/test/kool/html/tokool/HtmlToKoolTest.kt#L6) which takes this [sample.html](https://github.com/koolio/kool/blob/master/kool-html-parse/src/test/resources/sample.html#L1) and generates [this template](https://github.com/koolio/kool/blob/master/kool-html-parse/src/test/kotlin/generated/GeneratedTemplate.kt#L7) to see how its done.
