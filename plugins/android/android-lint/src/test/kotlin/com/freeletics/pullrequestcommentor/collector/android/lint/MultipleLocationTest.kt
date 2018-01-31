package com.freeletics.pullrequestcommentor.collector.android.lint

import com.freeletcis.pullrequestcommentor.collecltor.android.lint.AndroidLintPlugin
import com.freeletics.pullrequestcommentor.collector.PluginResult
import org.amshove.kluent.shouldNotEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

object MultipleLocationTest : Spek({

    given("an lint file containing issues with multiple locations") {

        it("creates no comments") {
            val plugin = AndroidLintPlugin(
                    servityStr = "Error",
                    basePathToRemoveFromLocation = "/Users/ci/some-app/",
                    lintResultFileMatchers = listOf("*lint_results_multiple_locations1.xml", "*int_results_multiple_locations2.xml"),
                    startingDirectoryToScanString = "src/test/resources/"
            )

            val comments = plugin.getComments()
            val expected = PluginResult.ErrorComments(listOf(

            ))
            comments shouldNotEqual expected
        }
    }
})