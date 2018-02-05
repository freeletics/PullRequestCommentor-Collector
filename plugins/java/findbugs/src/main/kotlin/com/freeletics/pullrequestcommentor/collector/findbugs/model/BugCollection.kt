package com.freeletics.pullrequestcommentor.collector.findbugs.model

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Path
import com.tickaroo.tikxml.annotation.Xml

@Xml
data class BugCollection (
    @Path("Project") @Element val sources :  List<SrcDir>
)