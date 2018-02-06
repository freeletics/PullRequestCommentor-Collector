package com.freeletics.pullrequestcomment.collector.checkstyle

import com.freeletics.pullrequestcommentor.collector.FileLineComment
import com.freeletics.pullrequestcommentor.collector.PluginResult
import com.freeletics.pullrequestcommentor.collector.checkstyle.CheckstyleLintPlugin
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

object MultipleErrorsInFileTest : Spek({

    given("two checkstyle files with 2 errors and 1 error") {
        it("should create comments file with 3 comments") {

            val plugin = CheckstyleLintPlugin(
                    severityStr = "error",
                    basePathToRemoveFromLocation = "/Users/foo",
                    lintResultFileMatchers = listOf("*checkstyle_multiple_errors_in_file1.xml", "*checkstyle_multiple_errors_in_file2.xml"),
                    startingDirectoryToScanString = "src/test/resources/"
            )

            val expectedComments = PluginResult.ErrorComments(listOf(
                FileLineComment(commentText = "Reported by detekt.TooManyFunctions\n\nClass 'FooClass' with '14' functions detected. Allowed maximum amount of functions inside classes is set to '10'",
                        filePath = "foo/app/src/main/java/com/example/Example.kt",
                        lineNumber = 56),
                    FileLineComment(commentText = "Reported by detekt.TooGenericExceptionCaught\n\nToo generic",
                        filePath = "foo/app/src/main/java/com/example/Example.kt",
                        lineNumber = 98),
                    FileLineComment(commentText = "Other Example",
                        filePath = "foo/app/src/main/java/com/example/Other.kt",
                        lineNumber = 23)
            ))

            plugin.getComments() shouldEqual expectedComments
        }
    }
})