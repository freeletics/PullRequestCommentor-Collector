package com.freeletcis.pullrequestcommentor.collecltor.android.lint.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "issue")
data class AndroidLintIssue(
        @Attribute val severity: String,
        @Attribute val message: String,
        @Attribute val explanation: String?,
        @Attribute val errorLine1: String?,
        @Attribute val errorLine2: String?,
        @Element(name = "location") val locations: List<AndroidLintLocation>

)