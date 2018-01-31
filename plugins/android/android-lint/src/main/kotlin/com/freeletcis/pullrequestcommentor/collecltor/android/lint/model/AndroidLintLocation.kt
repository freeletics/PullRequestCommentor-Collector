package com.freeletcis.pullrequestcommentor.collecltor.android.lint.model

import com.tickaroo.tikxml.annotation.Attribute
import com.tickaroo.tikxml.annotation.Xml

@Xml(name = "location")
data class AndroidLintLocation (
        @Attribute val file : String,
        @Attribute val line : Int
)