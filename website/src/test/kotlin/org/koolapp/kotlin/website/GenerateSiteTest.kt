package io.kool.website

import java.io.File
import kotlin.test.*
import org.junit.Test as test

class GenerateSiteTest {
    val srcDir = findTemplateDir()
    val siteOutputDir = File(srcDir, "../../../target/site")

    val version = System.getProperty("project.version") ?: "SNAPSHOT"
    val versionDir = if (version.contains("SNAPSHOT")) "snapshot" else version

    test fun generateSite(): Unit {
        val generator = SiteGenerator(srcDir, siteOutputDir)
        generator.run()
    }

    test fun copyApiDocs(): Unit {
        val apidocDir = File(siteOutputDir, "../../../apidoc/target/site/apidocs")
        assertTrue(apidocDir.exists(), "Directory does not exist ${apidocDir.getCanonicalPath()}")

        val outDir = File(siteOutputDir, "versions/$versionDir/apidocs")
        println("Copying API docs to $outDir")

        copyDocResources(outDir)
        copyRecursive(apidocDir, outDir)
    }

    fun copyDocResources(outDir: File): Unit {
        val sourceDir = File(srcDir, "../apidocs")
        copyRecursive(sourceDir, outDir)
    }


    // TODO this would make a handy extension function on File :)
    fun copyRecursive(sourceDir: File, outDir: File): Unit {
        sourceDir.recurse {
            if (it.isFile()) {
                var relativePath = sourceDir.relativePath(it)
                val outFile = File(outDir, relativePath)
                outFile.directory.mkdirs()
                it.copyTo(outFile)
            }
        }
    }


    fun findTemplateDir(): File {
        val path = "src/main/webapp"
        for (p in arrayList(".", "apidocs", "library/apidocs")) {
            val sourceDir = File(".", path)
            if (sourceDir.exists()) {
                return sourceDir
            }
        }
        throw IllegalArgumentException("Could not find template directory: $path")
    }
}