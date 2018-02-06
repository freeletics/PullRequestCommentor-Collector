package com.freeletics.pullrequestcomment.collector.checkstyle

import com.freeletics.pullrequestcommentor.collector.PluginResult
import com.freeletics.pullrequestcommentor.collector.checkstyle.CheckstyleLintPlugin
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it

object EmptyResultTest : Spek({

    given("a checkstyle file with no errors"){
        it ("should create an empty comments file"){

            val plugin = CheckstyleLintPlugin(
                    severityStr = "error",
                    basePathToRemoveFromLocation = "/Users/foo",
                    lintResultFileMatchers = listOf("*lint-results-empty.xml"),
                    startingDirectoryToScanString = "src/test/resources/"
            )

            plugin.getComments() shouldEqual PluginResult.NoComment
        }
    }
})