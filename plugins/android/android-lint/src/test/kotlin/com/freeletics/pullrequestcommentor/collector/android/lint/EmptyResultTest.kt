package com.freeletics.pullrequestcommentor.collector.android.lint

import com.freeletcis.pullrequestcommentor.collecltor.android.lint.AndroidLintPlugin
import com.freeletics.pullrequestcommentor.collector.PluginResult
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

object EmptyResultTest : Spek({

    given("an lint file containing no errors") {

        it("creates no comments") {
            val plugin = AndroidLintPlugin(
                    servityStr = "Error",
                    basePathToRemoveFromLocation = "/Users/foo",
                    lintResultFileMatchers = listOf("\\*/lint\\-results\\-empty.xml"),
                    startingDirectoryToScanString = "src/test/resources/"
            )

            plugin.getComments() shouldEqual PluginResult.NoComment
        }
    }
})