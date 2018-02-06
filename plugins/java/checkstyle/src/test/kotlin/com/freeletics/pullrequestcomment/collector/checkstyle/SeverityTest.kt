package com.freeletics.pullrequestcomment.collector.checkstyle

import com.freeletics.pullrequestcommentor.collector.FileLineComment
import com.freeletics.pullrequestcommentor.collector.PluginResult
import com.freeletics.pullrequestcommentor.collector.checkstyle.CheckstyleLintPlugin
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object SeverityTest : Spek({

    given("a checkstyle file with errors of error severity, warning and information") {

        val error =  FileLineComment(commentText = "An error",
                filePath = "app/src/main/java/com/example/ExampleError.kt",
                lineNumber = 1)

        val warning =  FileLineComment(commentText = "A warning",
                filePath = "app/src/main/java/com/example/ExampleWarning.kt",
                lineNumber = 2)

        val information =  FileLineComment(commentText = "A information",
                filePath = "app/src/main/java/com/example/ExampleInformation.kt",
                lineNumber = 3)

        val ignoring =  FileLineComment(commentText = "ignoring",
                filePath = "app/src/main/java/com/example/ExampleIgnore.kt",
                lineNumber = 4)

        on("setting severity to error") {
            it("should create 1 comment") {

                val plugin = CheckstyleLintPlugin(
                        severityStr = "error",
                        basePathToRemoveFromLocation = "foo/",
                        lintResultFileMatchers = listOf("*checkstyle_severity.xml"),
                        startingDirectoryToScanString = "src/test/resources/"
                )

                val expectedComments = PluginResult.ErrorComments(listOf(error))

                plugin.getComments() shouldEqual expectedComments
            }
        }


        on("setting severity to warning") {
            it("should create 2 comments") {

                val plugin = CheckstyleLintPlugin(
                        severityStr = "warning",
                        basePathToRemoveFromLocation = "foo/",
                        lintResultFileMatchers = listOf("*checkstyle_severity.xml"),
                        startingDirectoryToScanString = "src/test/resources/"
                )

                val expectedComments = PluginResult.ErrorComments(listOf(error, warning))

                plugin.getComments() shouldEqual expectedComments
            }
        }


        on("setting severity to info") {
            it("should create 3 comments") {

                val plugin = CheckstyleLintPlugin(
                        severityStr = "info",
                        basePathToRemoveFromLocation = "foo/",
                        lintResultFileMatchers = listOf("*checkstyle_severity.xml"),
                        startingDirectoryToScanString = "src/test/resources/"
                )

                val expectedComments = PluginResult.ErrorComments(listOf(error, warning, information))

                plugin.getComments() shouldEqual expectedComments
            }
        }


        on("setting severity to ignore") {
            it("should create 4 comments") {

                val plugin = CheckstyleLintPlugin(
                        severityStr = "ignore",
                        basePathToRemoveFromLocation = "foo/",
                        lintResultFileMatchers = listOf("*checkstyle_severity.xml"),
                        startingDirectoryToScanString = "src/test/resources/"
                )

                val expectedComments = PluginResult.ErrorComments(listOf(error, warning, information, ignoring))

                plugin.getComments() shouldEqual expectedComments
            }
        }
    }
})