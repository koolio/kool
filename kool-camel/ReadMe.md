## Kool Camel

**Kool Camel** provides a [kotlin](http://jetbrains.github.com/kotlin/) based DSL for [Apache Camel](http://camel.apache.org/) which is a powerful extension to the Java DSL with Kool Expressions together with adding integration with [Kool Streams](http://kool.io/streams.html) to provide a typesafe Camel Integration and Event Processing DSL.

### Kool Expressions

The power of the Kool Camel DSL comes from the use of Kool Expressions. These are anonymous function blocks
used wherever you can use Camel Expression in the Java DSL. The function blocks take an implicit Camel [Exchange](http://camel.apache.org/maven/current/camel-core/apidocs/org/apache/camel/Exchange.html) object which lets you immediately refer to any of the existing [exchange methods](http://camel.apache.org/maven/current/camel-core/apidocs/org/apache/camel/Exchange.html) together with the [extension methods on Exchange](http://kool.io/versions/snapshot/apidocs/io/kool/camel/org/apache/camel/Exchange-extensions.html) without requiring extra noise like a '_' expression in Scala, an 'it' variable in Groovy or a named parameter declaration and variable in Java 8 lambdas which keeps the DSL nice and DRY.

For example you can refer to the **input** property to get the inbound [Message](http://camel.apache.org/maven/current/camel-core/apidocs/org/apache/camel/Message.html) or **out** for the outbound message and refer to headers or properties using the [subscript operators](https://github.com/koolio/kool/blob/master/kool-camel/src/test/kotlin/test/kool/camel/TransformAndSetHeaderTest.kt#L14)


### Kool Camel DSL examples

Here are various example patterns using the Kool Camel DSL to show you how DRY it is while still being very easy to read (even if you've never used kotlin before) and how Kool Expressions are integrated into the entire Java DSL:

* [message filter](https://github.com/koolio/kool/blob/master/kool-camel/src/test/kotlin/test/kool/camel/FilterThenTest.kt#L12)
* [content based router](https://github.com/koolio/kool/blob/master/kool-camel/src/test/kotlin/test/kool/camel/ChoiceTest.kt#L15)
* [message translator](https://github.com/koolio/kool/blob/master/kool-camel/src/test/kotlin/test/kool/camel/TransformTest.kt#L12)
* [message translator and setting headers in the same block](https://github.com/koolio/kool/blob/master/kool-camel/src/test/kotlin/test/kool/camel/TransformAndSetHeaderTest.kt#L12)
* [recipient list](https://github.com/koolio/kool/blob/master/kool-camel/src/test/kotlin/test/kool/camel/RecipientListTest.kt#L9)
* [process block](https://github.com/koolio/kool/blob/master/kool-camel/src/test/kotlin/test/kool/camel/ProcessTest.kt#L12)

### Camel and Kool Streams

Kool Camel allows you to turn any Camel [Endpoint](http://camel.apache.org/maven/current/camel-core/apidocs/org/apache/camel/Endpoint.html) into a <a href="http://kool.io/versions/snapshot/apidocs/io/kool/stream/Stream.html">Stream&lt;T&gt;</a> using a number of [helper methods on the Camel Endpoint](http://kool.io/versions/snapshot/apidocs/io/kool/camel/org/apache/camel/Endpoint-extensions.html) class. Once you have a typesafe Stream\<T\> you can then compose event processing steps (filtering, transforming, grouping, buffering, windowing, merging, correlating etc) with [Kool Streams](http://kool.io/streams.html) to perform powerful complex event processing.

On the flip side, you can easily send events on a [Kool Stream](http://kool.io/streams.html) to any Camel [Endpoint](http://camel.apache.org/maven/current/camel-core/apidocs/org/apache/camel/Endpoint.html) using the <a href="http://kool.io/versions/snapshot/apidocs/io/kool/camel/io/kool/stream/Stream-extensions.html#to(org.apache.camel.Endpoint)">to(Endpoint)</a> extension method.

This allows us to easily compose any [Kool Streams](http://kool.io/streams.html) and Camel Endpoints and Routes together to combine powerful integration and complex event processing capabilities into a single simple type safe DSL that works directly on your domain model and generates fast statically typed java bytecode without the need for reflection or dynamic invocation.

While the Kool Camel and Stream DSLs are Kotlin based; they obviously can easily work with any JVM code for domain models, functions and services; so its easy to reuse Kool Camel routes and Streams from Java, Scala, Groovy, JavaScript, JRuby in addition to the Kotlin language.

### IDE support

If you've not tried Kotlin yet, try the [Getting Started Guide](http://confluence.jetbrains.net/display/Kotlin/Getting+Started) to install the IDE plugin.

