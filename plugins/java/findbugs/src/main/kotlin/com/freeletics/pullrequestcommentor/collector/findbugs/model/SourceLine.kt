package com.freeletics.pullrequestcommentor.collector.findbugs.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

@Xml
data class SourceLine (
     @Attribute val start : Int?
)