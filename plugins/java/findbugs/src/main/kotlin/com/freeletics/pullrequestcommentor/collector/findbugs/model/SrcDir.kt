package com.freeletics.pullrequestcommentor.collector.findbugs.model

import com.tickaroo.tikxml.annotation.TextContent
import com.tickaroo.tikxml.annotation.Xml

@Xml
data class SrcDir (
    @TextContent val filePaht : String
)