package com.freeletics.pullrequestcommentor.collector.model.xml

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "codelinecomment")
internal data class XmlFileLineComment(
        @Attribute val filePath : String,
        @Attribute val lineNumber : Int,
        @TextContent(writeAsCData = true) val comment : String
) : XmlComment