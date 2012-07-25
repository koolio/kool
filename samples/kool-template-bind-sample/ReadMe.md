# Kool Template Bind Sample

This sample shows how to perform real time [Kool Templates](http://kool.io/templates.html), updating the DOM in place inside the template

You can run this sample in a web browser using JavaScript, or in a rich Java client with JavaFX.

Here's a breakdown of the source code used to implement this:

* [test.kool.myapp.MyApp.kt](https://github.com/koolio/kool/blob/master/samples/kool-template-bind-sample/src/main/kotlin/test/kool/myapp/MyApp.kt) the actual application which interacts with the DOM using the standard [kotlin.browser](http://jetbrains.github.com/kotlin/versions/snapshot/apidocs/kotlin/browser/package-summary.html) package.
* [sampleTemplate()](https://github.com/koolio/kool/blob/master/samples/kool-template-bind-sample/src/main/kotlin/test/kool/myapp/MyApp.kt#L24) a function which is a simple [Kool Template](http://kool.io/templates.html) that generates a DOM fragment in the client including listening to click events on the button (see the use of the [onClick() function on the button](https://github.com/koolio/kool/blob/master/samples/kool-template-bind-sample/src/main/kotlin/test/kool/myapp/MyApp.kt#L35))


## Using a web browser

Just run a build, then open the sample.html

    mvn install
    open sample.html


## Using Kool JavaFX

**NOTE** JavaFX requires [Java 7 update 4](http://www.oracle.com/technetwork/java/javase/overview/index.html) or later which ships with JavaFX. If you install Java 7 and use a Mac you might want to run this first...

    export JAVA_HOME=/Library/Java/JavaVirtualMachines/1.7.0.jdk/Contents/Home
    export PATH=$JAVA_HOME/bin:$PATH

You can check you have JavaFX in your JDK install via

    ls -l $JAVA_HOME/jre/lib/jfxrt.jar

which should find the JavaFX runtime jar (jfxrt.jar).

### Running the sample

To run the sample try...

    mvn -Pui

Assuming you've Java 7 enabled and JAVA_HOME points to the JRE/JDK for Java 7 or later.

Here is [how the JavaFX code works](https://github.com/koolio/kool/blob/master/samples/kool-template-sample/ReadMe.md)