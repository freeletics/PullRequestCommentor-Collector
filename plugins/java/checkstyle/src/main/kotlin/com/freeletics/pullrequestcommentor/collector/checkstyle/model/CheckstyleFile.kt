package com.freeletics.pullrequestcommentor.collector.checkstyle.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "file")
data class CheckstyleFile(
        @Attribute(name = "name") val filePath : String,
        @Element(name = "error") val errors : List<CheckstyleError>?

)