package com.freeletics.pullrequestcommentor.collector.checkstyle

import com.freeletcis.pullrequestcommentor.collector.utils.htmlescape.HtmlEscape
import com.freeletics.pullrequestcommentor.collector.CollectorPlugin
import com.freeletics.pullrequestcommentor.collector.Comment
import com.freeletics.pullrequestcommentor.collector.FileLineComment
import com.freeletics.pullrequestcommentor.collector.PluginResult
import com.freeletics.pullrequestcommentor.collector.checkstyle.model.*
import com.freeletics.pullrequestcommentor.collector.utils.filefinder.FileFinder
import com.tickaroo.tikxml.TikXml
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import okio.Okio
import java.io.File
import java.io.FileNotFoundException
import java.nio.file.Path

/**
 * Android Lint collector plugin that reads lint-result.xml
 */
class CheckstyleLintPlugin(
        severityStr: String,
        private val basePathToRemoveFromLocation: String,
        startingDirectoryToScanString: String,
        private val lintResultFileMatchers: List<String>
) : CollectorPlugin {

    private val filterServityBelow = CheckstyleSeverityConverter().read(severityStr)!! // Throws exception if string is not correct
    private val startingDirectoryToScan: Path = File(startingDirectoryToScanString).toPath()

    init {
        // Just some sanity check. this will throw an exception if a unexpected value is used.

        if (!File(startingDirectoryToScanString).exists()) {
            throw FileNotFoundException("The starting directory to scan for android lint results does not exist: $startingDirectoryToScanString")
        }

    }

    override fun getComments(): PluginResult {
        val tikxml = TikXml.Builder()
                .exceptionOnUnreadXml(false)
                .addTypeAdapter(Checkstyle::class.java, `Checkstyle$$TypeAdapter`())
                .addTypeAdapter(CheckstyleError::class.java, `CheckstyleError$$TypeAdapter`())
                .addTypeAdapter(CheckstyleFile::class.java, `CheckstyleFile$$TypeAdapter`())
                .build()

        val filesSingles = FileFinder.find(startingDirectoryToScan, lintResultFileMatchers).map {
            readFile(tikxml = tikxml, file = it.toFile())
        }

        val comments: List<Comment> = Single.concat(filesSingles).toList().blockingGet().flatMap { it }
        return if (comments.isEmpty())
            PluginResult.NoComment
        else PluginResult.ErrorComments(comments)
    }


    /**
     * Reads all files and transforms them to a Rx Single executable.
     */
    private fun readFile(tikxml: TikXml, file: File) = Single.fromCallable<List<Comment>> {

        val report = Okio.buffer(Okio.source(file)).use { fileSource ->
            tikxml.read(fileSource, Checkstyle::class.java)
        }

        if (report.filesWithErrors != null) {
            report.filesWithErrors
                    .flatMap(this::toFileLineComments)
        } else {
            emptyList()
        }
    }.subscribeOn(Schedulers.io())


    /**
     * Transforms a android lint fileContainingErrors with locations (there could be more than one location) into a FileLineComment
     */
    private fun toFileLineComments(fileContainingErrors: CheckstyleFile): List<Comment> {

        if (fileContainingErrors.errors == null)
            return emptyList()

        val filePath = fileContainingErrors.filePath.removePrefix(basePathToRemoveFromLocation)

        return fileContainingErrors.errors
                .filter { filterServityBelow.isHigherOrEqualPrio(it.severity) }
                .map { error ->
                    FileLineComment(
                            filePath = filePath,
                            lineNumber = error.line,
                            commentText = (if (error.source != null) "Reported by ${error.source}\n\n" else "") + error.message.unescapeHtml()
                    )
                }
    }


    private fun String.unescapeHtml() = HtmlEscape.unescapeHtml3(this)
}

