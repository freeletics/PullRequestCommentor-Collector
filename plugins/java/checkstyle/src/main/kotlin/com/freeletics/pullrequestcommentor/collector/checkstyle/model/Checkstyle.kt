package com.freeletics.pullrequestcommentor.collector.checkstyle.model

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml
data class Checkstyle (
    @Element(name = "file") val filesWithErrors : List<CheckstyleFile>?
)