package com.freeletcis.pullrequestcommentor.collecltor.android.lint

import com.freeletcis.pullrequestcommentor.collecltor.android.lint.model.*
import com.freeletcis.pullrequestcommentor.collector.utils.htmlescape.HtmlEscape
import com.freeletics.pullrequestcommentor.collector.CollectorPlugin
import com.freeletics.pullrequestcommentor.collector.Comment
import com.freeletics.pullrequestcommentor.collector.FileLineComment
import com.freeletics.pullrequestcommentor.collector.PluginResult
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
class AndroidLintPlugin(
        servityStr: String,
        private val basePathToRemoveFromLocation: String,
        startingDirectoryToScanString: String,
        private val lintResultFileMatchers: List<String>
) : CollectorPlugin {

    private val filterServityBelow = servityStr.toServityInt()
    private val startingDirectoryToScan: Path = File(startingDirectoryToScanString).toPath()

    init {
        // Just some sanity check. this will throw an exception if a unexpected value is used.
        AndroidLintServity.valueOf(servityStr)

        if (!File(startingDirectoryToScanString).exists()) {
            throw FileNotFoundException("The starting directory to scan for android lint results does not exist: $startingDirectoryToScanString")
        }

    }

    override fun getComments(): PluginResult {
        val tikxml = TikXml.Builder()
                .exceptionOnUnreadXml(false)
                .addTypeAdapter(AndroidLintIssue::class.java, `AndroidLintIssue$$TypeAdapter`())
                .addTypeAdapter(AndroidLintLocation::class.java, `AndroidLintLocation$$TypeAdapter`())
                .addTypeAdapter(AndroidLintReport::class.java, `AndroidLintReport$$TypeAdapter`())
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
            tikxml.read(fileSource, AndroidLintReport::class.java)
        }

        if (report.issues != null) {
            report.issues
                    .filter { it.severity.toServityInt() <= filterServityBelow }
                    .flatMap(this::toFileLineComments)
        } else {
            emptyList()
        }
    }.subscribeOn(Schedulers.io())


    /**
     * Used to check which level we should filter
     */
    private fun String.toServityInt() = when (this) {
        "Fatal" -> 1
        "Error" -> 2
        "Warning" -> 3
        "Information" -> 4
        else -> 5 // Should never be the case
    }

    /**
     * Transforms a android lint issue with locations (there could be more than one location) into a FileLineComment
     */
    private fun toFileLineComments(issue: AndroidLintIssue): List<Comment> {
        val message = issue.message.unescapeHtml()
        val explanation = issue.explanation?.unescapeHtml()
        val errorLine1 = issue.errorLine1?.unescapeHtml()
        val errorLine2 = issue.errorLine2?.unescapeHtml()

        return issue.locations.map { location ->
            val filePath = location.file.removePrefix(basePathToRemoveFromLocation)
            val builder = StringBuilder(message)

            if (explanation != null && explanation.isNotEmpty()) {
                builder.append("\n\n")
                builder.append(explanation)
            }


            val hasErrorline1 = errorLine1 != null && errorLine1.isNotEmpty()
            val hasErrorline2 = errorLine2 != null && errorLine2.isNotEmpty()

            if (hasErrorline1 || hasErrorline2) {
                builder.append("\n\n```")

                if (hasErrorline1) {
                    builder.append("\n")
                    builder.append(errorLine1)
                }
                if (errorLine2 != null && errorLine2.isNotEmpty()) {
                    builder.append("\n")
                    builder.append(errorLine2)
                }

                builder.append("\n```")
            }

            FileLineComment(
                    filePath = filePath,
                    lineNumber = location.line,
                    commentText = builder.toString()
            )
        }
    }


    private fun String.unescapeHtml() = HtmlEscape.unescapeHtml3(this)
}

