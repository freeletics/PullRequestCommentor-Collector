package com.freeletics.pullrequestcommentor.collector

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.freeletics.pullrequestcommentor.collector.model.ConfigurationFile
import com.freeletics.pullrequestcommentor.collector.model.Output
import com.freeletics.pullrequestcommentor.collector.model.PluginToLoad
import com.freeletics.pullrequestcommentor.collector.model.xml.XmlComment
import com.freeletics.pullrequestcommentor.collector.model.xml.XmlComments
import com.freeletics.pullrequestcommentor.collector.model.xml.XmlFileLineComment
import com.freeletics.pullrequestcommentor.collector.model.xml.XmlSimpleComment
import com.tickaroo.tikxml.TikXml
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.schedulers.Schedulers
import okio.Okio
import org.apache.commons.cli.DefaultParser
import org.apache.commons.cli.Options
import java.io.File
import java.io.IOException
import java.io.OutputStream
import java.io.PrintWriter

internal const val OPTION_CONFIG_FILE = "f"

fun main(args: Array<String>) {
    start(args = args,
            outputStream = System.out,
            errorStream = System.err,
            scheduler = Schedulers.io()
    )
}


/**
 * Prints [Output] on the given streams
 */
private fun printOutput(output: Output, outputStream: OutputStream, errorStream: OutputStream) {
    val printer = PrintWriter(outputStream, true)
    val errorPrinter = PrintWriter(errorStream, true)
    when (output) {
        is Output.Error -> errorPrinter.println(output.errorMessage)
        is Output.Successful -> printer.println(output.msg)
    }
}


/**
 * This method is mostly used as entrypoint to the application, mostly for testing
 */
internal fun start(args: Array<String>, outputStream: OutputStream, errorStream: OutputStream, scheduler: Scheduler) {
    val outputs = run(args = args, scheduler = scheduler)
    outputs.forEach {
        printOutput(output = it, outputStream = outputStream, errorStream = errorStream)
    }
}

/**
 * actually runs the application
 */
private fun run(args: Array<String>, scheduler: Scheduler): List<Output> {

    val options = Options()
    options.addOption(OPTION_CONFIG_FILE, true, "Path to the file containing the text for the comment that should be posted to the given github issue")

    val parser = DefaultParser()
    val cmd = parser.parse(options, args)

    val filePath = cmd.getOptionValue(OPTION_CONFIG_FILE)
            ?: return listOf(Output.Error("The path to the file which content should be posted is not set. Use -$OPTION_CONFIG_FILE option"))

    val file = File(filePath)
    if (!file.exists()) {
        return listOf(Output.Error("The specified config file '$filePath' does not exist"))
    }

    val config = readConfigFile(file)

    try {

        val fileToSave = File(config.outputFilePath)
        if (fileToSave.parentFile != null && !fileToSave.parentFile.exists()) {
            file.parentFile.mkdirs()
        }

        if (!file.exists()) {
            file.createNewFile()
        }

        return Observable.concat(buildExecutionGraph(
                plugins = config.pluginToLoad,
                scheduler = scheduler
        ))
                .toList()
                .map { writeXml(pluginResults = it, file = fileToSave, groupSuccessMessagesToOneComment = false) }
                .blockingGet()

    } catch (t: ConstructorNotFoundException) {
        return listOf(Output.Error("ERROR: " + t.message))
    } catch (t: MoreThanOneCollectorPluginInJarException) {
        return listOf(Output.Error("ERROR: " + t.message))
    } catch (t: NoCollectorPulingFoundException) {
        return listOf(Output.Error("ERROR: " + t.message))
    } catch (t: SecurityException) {
        t.printStackTrace()
        return listOf(Output.Error("ERROR: could not create the necessary directories for saving the file at ${config.outputFilePath}. See stacktrace above for more information,.ø"))
    } catch (t: IOException) {
        t.printStackTrace()
        return listOf(Output.Error("ERROR: An error has occurred while reading or writing files. See the stacktrace above."))
    }
}


/**
 * reads the configuration file
 */
private fun readConfigFile(file: File): ConfigurationFile {

    val mapper = ObjectMapper(YAMLFactory()).registerModule(KotlinModule())
    return mapper.readValue<ConfigurationFile>(file)
}


/**
 * Build the actual execution Graph for each Plugin. This method dertemines the order in which the Plugin has to be executed
 */
private fun buildExecutionGraph(plugins: List<PluginToLoad>, scheduler: Scheduler): List<Observable<PluginResult>> {

    val pluginClassLoader = CollectorPluginClassLoader()
    val instantiator = CollectorPluginInstantiator(pluginClassLoader)

    return plugins.map { it.toPluginResultObservable(instantiator, scheduler) }
}


/**
 * Creates the execution (sub graph) for the given plugin
 */
private fun PluginToLoad.toPluginResultObservable(collectorPluginInstantiator: CollectorPluginInstantiator, scheduler: Scheduler): Observable<PluginResult> {

    val observable: Observable<PluginResult> = Observable.fromCallable {
        val plugin = collectorPluginInstantiator.instantiate(this)

        plugin.getComments()
    }


    return if (pluginsToRunIfNoErrorCommentsFromThisPlugin == null || pluginsToRunIfNoErrorCommentsFromThisPlugin.isEmpty())
        observable.subscribeOn(scheduler)
    else {
        observable.concatMap {
            when (it) {
                is PluginResult.ErrorComments -> Observable.just(it) // Don't continue with the sub plugins, because error comments have been generated

                PluginResult.NoComment -> {
                    val subPlugins: Iterable<Observable<PluginResult>> = this.pluginsToRunIfNoErrorCommentsFromThisPlugin.map {
                        it.toPluginResultObservable(collectorPluginInstantiator, scheduler)
                                .subscribeOn(scheduler)
                    }
                    Observable.concat(subPlugins)
                }

                is PluginResult.SuccessComment -> {
                    val subPlugins: Iterable<Observable<PluginResult>> = this.pluginsToRunIfNoErrorCommentsFromThisPlugin.map {
                        it.toPluginResultObservable(collectorPluginInstantiator, scheduler)
                                .subscribeOn(scheduler)
                    }
                    Observable.concat(subPlugins).startWith(it)
                }
            }
        }
    }

}


/**
 * Writes the xml file for the list of plugins
 */
private fun writeXml(
        file: File,
        groupSuccessMessagesToOneComment: Boolean,
        pluginResults: List<PluginResult>
): List<Output> {
    val tikXml = TikXml.Builder()
            .writeDefaultXmlDeclaration(true)
            .build()

    val commentsToWrite: List<XmlComment> = if (groupSuccessMessagesToOneComment) {
        emptyList()
    } else {
        pluginResults.flatMap {
            when (it) {
                is PluginResult.NoComment -> emptyList()
                is PluginResult.SuccessComment -> listOf(it.comment)
                is PluginResult.ErrorComments -> it.errorComments
            }
        }.map(Comment::toXmlComment)
                .distinct()

    }

    Okio.buffer(Okio.sink(file)).use { fileSink ->
        return try {
            tikXml.write(fileSink, XmlComments(commentsToWrite))
            listOf(Output.Successful("Successfully written $file containing ${commentsToWrite.size} comments"))
        } catch (t: Throwable) {
            t.printStackTrace()
            listOf(Output.Error("An error has occurred while writing $file. See stacktrace above."))
        } finally {
            fileSink.emit()
        }
    }

}


/**
 * Converts Comments from Plugins to comments that can be written as xml
 */
private fun Comment.toXmlComment() = when (this) {
    is SimpleComment -> XmlSimpleComment(comment = commentText)
    is FileLineComment -> XmlFileLineComment(filePath = filePath, comment = commentText, lineNumber = lineNumber)
}
