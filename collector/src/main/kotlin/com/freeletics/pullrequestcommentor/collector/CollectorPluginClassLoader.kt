package com.freeletics.pullrequestcommentor.collector

import com.freeletics.pullrequestcommentor.collector.model.PluginToLoad
import java.io.File
import java.net.URLClassLoader
import java.util.concurrent.ConcurrentHashMap
import java.util.jar.JarFile


/**
 * Responsible to load the class file from a jar
 */
class CollectorPluginClassLoader {


    //  private val MANIFEST_COLLECTOR_PLUGIN_NAME = "CollectorPlugin"

    private val cache = ConcurrentHashMap<String, Class<CollectorPlugin>>()

    fun load(pluginToLoad: PluginToLoad): Class<CollectorPlugin> = cache.getOrPut(pluginToLoad.jar) {
        val urlToJar = pluginToLoad.jar
        /*
          val jarFile = JarFile(urlToJar)
          val manifest = jarFile.getManifest()
          val attributes = manifest.mainAttributes
          if (attributes == null) {

          }
          val collectorPluginClassName = attributes.getValue(MANIFEST_COLLECTOR_PLUGIN_NAME)
          if (collectorPluginClassName == null) {

          }
          */


        /*
        val jarStream: JarFile = if (urlToJar.startsWith("http")) {
            val u = URL("jar", "", urlToJar + "!/")
            val uc = u.openConnection() as JarURLConnection
            uc.jarFile
        } else
            JarFile(urlToJar)
            */




        val clazzLoader = URLClassLoader.newInstance(arrayOf(File(urlToJar).toURI().toURL()))
        val jarStream = JarFile(urlToJar)
        var collectorPlugin: Class<CollectorPlugin>? = null

        jarStream.entries().asSequence()
                .filter { !it.isDirectory && it.name.endsWith(".class") }
                .forEach { entry ->
                    // This ZipEntry represents a class. Now, what class does it represent?
                    val className = entry.name.replace('/', '.').removeSuffix(".class") // including ".class"

                    val loadedClass = Class.forName(className, true, clazzLoader)


                    val isColectorPlugin: Boolean = loadedClass.interfaces.firstOrNull() { it.canonicalName == CollectorPlugin::class.java.canonicalName } != null
                    if (isColectorPlugin) {

                        if (collectorPlugin == null)
                            collectorPlugin = loadedClass as Class<CollectorPlugin>
                        else
                            throw MoreThanOneCollectorPluginInJarException("The jar at $urlToJar contains more than one ${CollectorPlugin::class.simpleName}. Please contact the author of this plugin as only one Collector Plugin for each .jar is allowed.")
                    }

                }


        if (collectorPlugin == null) {
            throw NoCollectorPulingFoundException("No ${CollectorPlugin::class.simpleName} has been found in $urlToJar")
        }

        collectorPlugin!!
    }

}

/**
 * This exception is thrown if more than one CollectorPlugin has been found in the jar to load
 */
internal class MoreThanOneCollectorPluginInJarException(msg: String) : Exception(msg)

/**
 * This exception is thrown if a jar has been loaded which actually doesn't contain any CollectorPlugin
 */
internal class NoCollectorPulingFoundException(msg: String) : Exception(msg)