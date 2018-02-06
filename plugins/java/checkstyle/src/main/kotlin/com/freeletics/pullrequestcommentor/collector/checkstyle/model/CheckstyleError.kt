package com.freeletics.pullrequestcommentor.collector.checkstyle.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "error")
data class CheckstyleError(
        @Attribute val line : Int,
        @Attribute(converter = CheckstyleSeverityConverter::class) val severity : CheckstyleSeverity,
        @Attribute val message : String,
        @Attribute val source: String?
)