package com.freeletics.pullrequestcommentor.collector

import com.freeletics.pullrequestcommentor.collector.model.PluginToLoad
import java.lang.reflect.Type

/**
 * Responsible to actually instantiate a [CollectorPlugin] from a [PluginToLoad]
 */
internal class CollectorPluginInstantiator(
        private val collectorPluginClassLoader: CollectorPluginClassLoader
) {

    /**
     * Actually instantiates a [CollectorPlugin]
     */
    fun instantiate(pluginToLoad: PluginToLoad): CollectorPlugin {
        val classToInstantiate = collectorPluginClassLoader.load(pluginToLoad)

        val pluginParams = pluginToLoad.params
        // No parameters required, so we can use the default constructor
        if (pluginParams == null || (pluginParams.isEmpty())) {
            return classToInstantiate.newInstance()
        }

        // check for parameters and constructor
        val constructor = classToInstantiate.constructors.firstOrNull { constructor ->

            if (constructor.parameters.size != pluginParams!!.size)
                false

            // check if constructor params are from the same type as expected
            constructor.parameters.forEachIndexed { i, constructorParam ->
                val param = pluginParams[i]
                if (param.javaClass.isAssignable(
                                toClass = constructorParam.javaClass,
                                autoboxing = true))
                    return@firstOrNull false
            }

            true
        }

        if (constructor == null) {
            throw ConstructorNotFoundException("Could not found a constructor matching these parameters: $pluginParams for plugin $classToInstantiate")
        }

        return constructor.newInstance(*pluginParams!!) as CollectorPlugin
    }


}

/**
 * This kind of exception is thrown if no constructor has been found that matches the specified params from config file
 */
internal class ConstructorNotFoundException(msg: String) : Exception(msg)