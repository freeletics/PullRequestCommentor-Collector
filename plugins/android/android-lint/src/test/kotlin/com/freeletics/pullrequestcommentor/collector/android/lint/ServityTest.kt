package com.freeletics.pullrequestcommentor.collector.android.lint

import com.freeletcis.pullrequestcommentor.collecltor.android.lint.AndroidLintPlugin
import com.freeletics.pullrequestcommentor.collector.FileLineComment
import com.freeletics.pullrequestcommentor.collector.PluginResult
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object ServityTest : Spek({

    given("an lint file containing 3 issues with servity Error, Warning and Information each") {

        val issueWithErrorServity = FileLineComment(
                commentText = "Issue 1",
                lineNumber = 1,
                filePath = "src/main/res/layout/foo1.xml")

        val issueWithWarningServity = FileLineComment(
                commentText = "Issue 2",
                lineNumber = 2,
                filePath = "src/main/res/layout/foo2.xml")


        val issueWithInformationServity = FileLineComment(
                commentText = "Issue 3",
                lineNumber = 3,
                filePath = "src/main/res/layout/foo3.xml")


        on("android lint plugin servity set to Error") {

            it("creates 1 comment") {
                val plugin = AndroidLintPlugin(
                        servityStr = "Error",
                        basePathToRemoveFromLocation = "/Users/ci/some-app/",
                        lintResultFileMatchers = listOf("*lint_results_servity.xml"),
                        startingDirectoryToScanString = "src/test/resources/"
                )

                val comments = plugin.getComments()
                val expected = PluginResult.ErrorComments(listOf(issueWithErrorServity))

                comments shouldEqual expected
            }
        }


        on("android lint plugin servity set to Warning") {

            it("creates 2 comments") {
                val plugin = AndroidLintPlugin(
                        servityStr = "Warning",
                        basePathToRemoveFromLocation = "/Users/ci/some-app/",
                        lintResultFileMatchers = listOf("*lint_results_servity.xml"),
                        startingDirectoryToScanString = "src/test/resources/"
                )

                val comments = plugin.getComments()
                val expected = PluginResult.ErrorComments(listOf(issueWithErrorServity, issueWithWarningServity))

                comments shouldEqual expected
            }
        }

        on("android lint plugin servity set to Information") {

            it("creates 3 comments") {
                val plugin = AndroidLintPlugin(
                        servityStr = "Information",
                        basePathToRemoveFromLocation = "/Users/ci/some-app/",
                        lintResultFileMatchers = listOf("*lint_results_servity.xml"),
                        startingDirectoryToScanString = "src/test/resources/"
                )

                val comments = plugin.getComments()
                val expected = PluginResult.ErrorComments(listOf(issueWithErrorServity, issueWithWarningServity, issueWithInformationServity))
                
                comments shouldEqual expected
            }
        }
    }
})