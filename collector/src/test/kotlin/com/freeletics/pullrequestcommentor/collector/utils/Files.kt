package com.freeletics.pullrequestcommentor.collector.utils

import com.freeletics.pullrequestcommentor.collector.model.ConfigurationFile
import com.freeletics.pullrequestcommentor.collector.model.xml.XmlComments
import com.tickaroo.tikxml.TikXml
import okio.Okio
import org.amshove.kluent.shouldBeEmpty
import org.amshove.kluent.shouldEqual
import org.jetbrains.spek.api.Spek
import java.io.File
import java.nio.file.Files

private val tikXml = TikXml.Builder().writeDefaultXmlDeclaration(true).build()

internal fun Any.getResourcePath(resourceRelativePath: String) = File(ConfigurationFile::class.java.getClassLoader().getResource(resourceRelativePath).toURI())


internal fun readFile(file: File) = String(Files.readAllBytes(file.toPath()))


internal infix fun StringOutputStream.shouldEqualLine(expected: String) {
    asString() shouldEqual (expected + "\n")
}

internal fun StringOutputStream.shouldBeEmpty() {
    toString().shouldBeEmpty()
}

/**
 * Reads / parses the result xml file
 */
internal fun writtenXml(pathToCommentsXmlFile: String): WrittenXmlFile = Okio.buffer(Okio.source(File(pathToCommentsXmlFile))).use {
    WrittenXmlFile(tikXml.read(it, XmlComments::class.java))
}


internal data class WrittenXmlFile(internal val readComments: XmlComments) {

    infix fun shouldEqual(comments: XmlComments) {
        readComments shouldEqual comments
    }
}
