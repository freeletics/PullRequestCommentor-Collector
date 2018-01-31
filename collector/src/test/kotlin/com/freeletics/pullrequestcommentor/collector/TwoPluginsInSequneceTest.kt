package com.freeletics.pullrequestcommentor.collector

import com.freeletics.pullrequestcommentor.collector.model.xml.XmlComments
import com.freeletics.pullrequestcommentor.collector.model.xml.XmlFileLineComment
import com.freeletics.pullrequestcommentor.collector.model.xml.XmlSimpleComment
import com.freeletics.pullrequestcommentor.collector.utils.*
import com.tickaroo.tikxml.TikXml
import io.reactivex.schedulers.Schedulers
import org.amshove.kluent.shouldBeEqualTo
import org.amshove.kluent.shouldEqual
import org.amshove.kluent.shouldEqualTo
import org.jetbrains.spek.api.Spek
import org.jetbrains.spek.api.dsl.given
import org.jetbrains.spek.api.dsl.it
import org.jetbrains.spek.api.dsl.on

object TwoPluginsInSequneceTest : Spek({

    given("the collector with a config containing 2 plugins in sequence") {

        lateinit var outputStream: StringOutputStream
        lateinit var errorStream: StringOutputStream
        val resultPath = "build/comments.xml"

        beforeEachTest {
            outputStream = StringOutputStream()
            errorStream = StringOutputStream()
        }


        on("both plugins return NoComment") {

            TestCollectorWithParams.result = PluginResult.NoComment
            TestCollectorWithoutParams.result = PluginResult.NoComment


            it("writes an empty comments.xml file") {

                start(args = arrayOf("-f", "src/test/resources/config-two-plugins-in-sequence.yml"),
                        outputStream = outputStream,
                        errorStream = errorStream,
                        scheduler = Schedulers.single())


                // Check if reading parameters work as expected
                TestCollectorWithParams.aBoolean shouldEqualTo true
                TestCollectorWithParams.aDouble shouldEqualTo 2.0
                TestCollectorWithParams.aString shouldBeEqualTo "A string"
                TestCollectorWithParams.anInt shouldEqualTo 1

                errorStream.shouldBeEmpty()
                outputStream shouldEqualLine "Successfully written $resultPath containing 0 comments"

                writtenXml(resultPath) shouldEqual XmlComments(null)
            }
        }


        on("both plugins return error comments") {

            val errorsPlugin1 = PluginResult.ErrorComments(
                    listOf(SimpleComment("Error Comment from Plugin 1"),
                            FileLineComment(
                                    filePath = "some/path/Foo.java",
                                    lineNumber = 23,
                                    commentText = "Some File Line Comment from Plugin 1"
                            ))
            )

            val errorsPlugin2 = PluginResult.ErrorComments(
                    listOf(SimpleComment("Error Comment from Plugin 2"),
                            FileLineComment(
                                    filePath = "some/path/Bar.java",
                                    lineNumber = 42,
                                    commentText = "Some File Line Comment from Plugin 2"
                            ))
            )


            TestCollectorWithParams.result = errorsPlugin1
            TestCollectorWithoutParams.result = errorsPlugin2

            it("writes a comments.xml file containing 1 Simple Comment (plugin 1), 1 File Line Comment (plugin 1), 1 Simple Comment (plugin 2) and 1 File Line Comment (plugin 2)") {

                start(args = arrayOf("-f", "src/test/resources/config-two-plugins-in-sequence.yml"),
                        outputStream = outputStream,
                        errorStream = errorStream,
                        scheduler = Schedulers.single())


                // Check if reading parameters work as expected
                TestCollectorWithParams.aBoolean shouldEqualTo true
                TestCollectorWithParams.aDouble shouldEqualTo 2.0
                TestCollectorWithParams.aString shouldBeEqualTo "A string"
                TestCollectorWithParams.anInt shouldEqualTo 1

                errorStream.shouldBeEmpty()
                outputStream shouldEqualLine "Successfully written $resultPath containing 4 comments"

                writtenXml(resultPath) shouldEqual XmlComments(listOf(
                        XmlSimpleComment("Error Comment from Plugin 1"),
                        XmlFileLineComment(filePath = "some/path/Foo.java",
                                lineNumber = 23,
                                comment = "Some File Line Comment from Plugin 1"),
                        XmlSimpleComment("Error Comment from Plugin 2"),
                        XmlFileLineComment(filePath = "some/path/Bar.java",
                                lineNumber = 42,
                                comment = "Some File Line Comment from Plugin 2")
                ))
            }


        }



        on("both plugins return success comments") {

            val successPlugin1 = PluginResult.SuccessComment(SimpleComment("Success Comment from Plugin 1"))
            val successPlugin2 = PluginResult.SuccessComment(SimpleComment("Success Comment from Plugin 2"))



            TestCollectorWithParams.result = successPlugin1
            TestCollectorWithoutParams.result = successPlugin2

            it("writes a comments.xml file containing two success comments") {

                start(args = arrayOf("-f", "src/test/resources/config-two-plugins-in-sequence.yml"),
                        outputStream = outputStream,
                        errorStream = errorStream,
                        scheduler = Schedulers.single())


                // Check if reading parameters work as expected
                TestCollectorWithParams.aBoolean shouldEqualTo true
                TestCollectorWithParams.aDouble shouldEqualTo 2.0
                TestCollectorWithParams.aString shouldBeEqualTo "A string"
                TestCollectorWithParams.anInt shouldEqualTo 1

                errorStream.shouldBeEmpty()
                outputStream shouldEqualLine "Successfully written $resultPath containing 2 comments"

                writtenXml(resultPath) shouldEqual XmlComments(listOf(
                        XmlSimpleComment("Success Comment from Plugin 1"),
                        XmlSimpleComment("Success Comment from Plugin 2")
                ))
            }


        }



        on("first plugin returns error other returns success comments") {

            val successPlugin1 = PluginResult.SuccessComment(SimpleComment("Success Comment from Plugin 1"))
            val errorsPlugin2 = PluginResult.ErrorComments(
                    listOf(SimpleComment("Error Comment from Plugin 2"),
                            FileLineComment(
                                    filePath = "some/path/Bar.java",
                                    lineNumber = 42,
                                    commentText = "Some File Line Comment from Plugin 2"
                            ))
            )



            TestCollectorWithParams.result = successPlugin1
            TestCollectorWithoutParams.result = errorsPlugin2

            it("writes a comments.xml file containing 1 success comments (plugin 1) and 2 error comments") {

                start(args = arrayOf("-f", "src/test/resources/config-two-plugins-in-sequence.yml"),
                        outputStream = outputStream,
                        errorStream = errorStream,
                        scheduler = Schedulers.single())


                // Check if reading parameters work as expected
                TestCollectorWithParams.aBoolean shouldEqualTo true
                TestCollectorWithParams.aDouble shouldEqualTo 2.0
                TestCollectorWithParams.aString shouldBeEqualTo "A string"
                TestCollectorWithParams.anInt shouldEqualTo 1

                errorStream.shouldBeEmpty()
                outputStream shouldEqualLine "Successfully written $resultPath containing 3 comments"

                writtenXml(resultPath) shouldEqual XmlComments(listOf(
                        XmlSimpleComment("Success Comment from Plugin 1"),
                        XmlSimpleComment("Error Comment from Plugin 2"),
                        XmlFileLineComment(filePath = "some/path/Bar.java",
                                lineNumber = 42,
                                comment = "Some File Line Comment from Plugin 2")
                ))
            }


        }
    }

})