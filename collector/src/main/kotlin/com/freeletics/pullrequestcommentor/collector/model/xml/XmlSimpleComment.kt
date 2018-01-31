package com.freeletics.pullrequestcommentor.collector.model.xml

import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "comment")
internal data class XmlSimpleComment(
        @TextContent(writeAsCData = true) val comment : String
) : XmlComment