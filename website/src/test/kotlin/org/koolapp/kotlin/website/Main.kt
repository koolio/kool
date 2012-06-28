package io.kool.kotlin.website

import org.eclipse.jetty.server.Server
import org.eclipse.jetty.webapp.*
import org.eclipse.jetty.annotations.ClassNameResolver
import org.eclipse.jetty.annotations.AnnotationParser
import org.eclipse.jetty.annotations.AnnotationConfiguration
import org.eclipse.jetty.util.resource.Resource
import org.eclipse.jetty.util.resource.FileResource
import org.eclipse.jetty.plus.webapp.PlusConfiguration
import org.eclipse.jetty.plus.webapp.EnvConfiguration
import org.mortbay.jetty.plugin.JettyWebAppContext

import java.io.File
import java.util.*


/**
 * Returns true if the file exists
 */
fun fileExists(path: String): Boolean {
    val file = File(path)
    return file.exists() && file.isFile()
}

/**
 * Returns true if the directory exists
 */
fun directoryExists(path: String): Boolean {
    val file = File(path)
    return file.exists() && file.isDirectory()
}

/**
 * Runs the web app
 */
fun main(args: Array<String>): Unit {
    val port = 8080
    val contextPath = "/"
    var path = "src/main/webapp"
    if (!directoryExists(path)) {
        path = "website/$path"
        require(directoryExists(path), "No webapp path could be found for $path")
    }
    val webXml = path + "/WEB-INF/web.xml"
    require(fileExists(webXml), "No web.xml could be found for $webXml")

    println("Connect via http://localhost:$port$contextPath using web app path: $path")

    /** Returns true if we should scan this lib for annotations */
    fun isScannedWebInfLib(path: String): Boolean {
        return path.endsWith("kool/website/target/classes")
        //return path.contains("kool")
        //return true
    }

    val pathSeparator = File.pathSeparator ?: ":"

    val classpath = System.getProperty("java.class.path") ?: ""
    val classpaths = classpath.split(pathSeparator)
    val jarNames: Collection<String> = classpaths.filter{ isScannedWebInfLib(it) }

    // TODO remove the File? stuff and null checks when issue fixed:
    val files = jarNames.map<String, File?>{ File(it) }.filter{ it != null && it.exists() }

    // TODO remove .toList() when filter() returns List
    val jars = files.filter{ it != null && it.isFile() }.toSet()
    val extraClassDirs = files.filter{ it != null && it.isDirectory() }.toList()

    println("Using WEB-INF/lib jars: $jars and classes dirs: $extraClassDirs")

    val annotationConfiguration = object : AnnotationConfiguration() {

        public override fun parseWebInfClasses(context: WebAppContext?, parser: AnnotationParser?) {
            super.parseWebInfClasses(context, parser)

            if (parser != null && context != null) {
                for (file in extraClassDirs) {
                    val classesDir = FileResource(file?.toURL())
                    parser.parse(classesDir, MyResolver(context))

                    val annotations = ArrayList<DiscoveredAnnotation?>();
                    //TODO - where to set the annotations discovered from WEB-INF/classes?
                    gatherAnnotations(annotations, parser.getAnnotationHandlers());

                    if (annotations.notEmpty()) {
                        println("From $file found annotations $annotations")
                    }
                    context.getMetaData()?.addDiscoveredAnnotations (annotations);
                }
            }
        }
    }


    val context = JettyWebAppContext()
    context.setWebInfLib(jars.toList())
    context.setConfigurations(array(
        annotationConfiguration, WebXmlConfiguration(),
            WebInfConfiguration()
    /*
            PlusConfiguration(),
            EnvConfiguration(),
            MetaInfConfiguration(),
            FragmentConfiguration()
    */
    ))

    context.setDescriptor(webXml)
    context.setResourceBase(path)
    context.setContextPath(contextPath)
    context.setParentLoaderPriority(true)

    val server = Server(port)
    server.setHandler(context)

    server.start()
    server.join()
}

class MyResolver(val context: WebAppContext): ClassNameResolver {
    public override fun isExcluded(name: String?): Boolean {
        if (context != null && context.isSystemClass(name)) return true
        if (context != null && context.isServerClass(name)) return false
        return false
    }
    public override fun shouldOverride(name: String?): Boolean {
        //looking at webapp classpath, found already-parsed class of same name - did it come from system or duplicate in webapp?
        if (context != null && context.isParentLoaderPriority())
            return false;
        return true;
    }
}