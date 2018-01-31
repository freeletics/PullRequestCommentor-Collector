package com.freeletics.pullrequestcommentor.collector.android.lint

import com.freeletcis.pullrequestcommentor.collecltor.android.lint.AndroidLintPlugin
import com.freeletics.pullrequestcommentor.collector.FileLineComment
import com.freeletics.pullrequestcommentor.collector.PluginResult
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

object MultipleLocationTest : Spek({

    given("an lint file containing issues with multiple locations") {

        it("creates no comments") {
            val plugin = AndroidLintPlugin(
                    servityStr = "Error",
                    basePathToRemoveFromLocation = "/Users/ci/some-app/",
                    lintResultFileMatchers = listOf("*lint_results_multiple_locations1.xml", "*lint_results_multiple_locations2.xml"),
                    startingDirectoryToScanString = "src/test/resources/"
            )

            val comments = plugin.getComments()
            val expected = PluginResult.ErrorComments(listOf(

                    // lint_results_multiple_locations2.xml
                    FileLineComment(commentText = "Other issue\n\nAn explanation",
                            lineNumber = 22,
                            filePath = "src/main/res/layout/bar1.xml"),

                    FileLineComment(commentText = "Other issue\n\nAn explanation",
                            lineNumber = 46,
                            filePath = "src/main/res/layout/bar2.xml"),

                    FileLineComment(commentText = "Another message",
                            lineNumber = 13,
                            filePath = "src/main/res/layout/bar3.xml"),


                    // lint_results_multiple_locations1.xml
                    FileLineComment(commentText = "Duplicate id @+id/foo\n\nexplanation line\n\n```\nerrorline1\nerrorline2\n```",
                            lineNumber = 22,
                            filePath = "src/main/res/layout/foo1.xml"),

                    FileLineComment(commentText = "Duplicate id @+id/foo\n\nexplanation line\n\n```\nerrorline1\nerrorline2\n```",
                            lineNumber = 46,
                            filePath = "src/main/res/layout/foo2.xml"),

                    FileLineComment(commentText = "Some message\n\nexplanation line\n\n```\nerrorline1\n```",
                            lineNumber = 1,
                            filePath = "src/main/res/layout/foo3.xml")

            ))
            
            comments shouldEqual expected
        }
    }
})