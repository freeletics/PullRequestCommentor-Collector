package com.freeletcis.pullrequestcommentor.collecltor.android.lint

import com.freeletcis.pullrequestcommentor.collecltor.android.lint.model.AndroidLintIssue
import com.freeletcis.pullrequestcommentor.collecltor.android.lint.model.AndroidLintReport
import com.freeletics.pullrequestcommentor.collector.CollectorPlugin
import com.freeletics.pullrequestcommentor.collector.Comment
import com.freeletics.pullrequestcommentor.collector.FileLineComment
import com.freeletics.pullrequestcommentor.collector.PluginResult
import com.freeletics.pullrequestcommentor.collector.android.lint.utils.StringUtils
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

        report.issues
                .filter { it.severity.toServityInt() <= filterServityBelow }
                .flatMap(this::toFileLineComments)
    }.subscribeOn(Schedulers.io())


    /**
     * Used to check which level we should filter
     */
    private fun String.toServityInt() = when (this) {
        "Error" -> 1
        "Warning" -> 2
        "Information" -> 3
        else -> 4 // Should never be the case
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

            explanation?.also {
                builder.append("\n\n")
                builder.append(it)
            }

            errorLine1?.also {
                builder.append("\n")
                builder.append(it)
            }
            errorLine2?.also {
                builder.append("\n")
                builder.append(it)
            }

            FileLineComment(
                    filePath = filePath,
                    lineNumber = location.line,
                    commentText = builder.toString()
            )
        }
    }


    private fun String.unescapeHtml() = StringUtils.unescapeHtml3(this)
}

