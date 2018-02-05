package com.freeletics.pullrequestcommentor.collector.findbugs.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.PropertyElement
import com.tickaroo.tikxml.annotation.Xml

@Xml
class BugInstance (
       @Attribute val category: String,
       @PropertyElement(name = "ShortMessage") val shortMessage : String,
       @PropertyElement(name = "LongMessage") val longMessage : String
)