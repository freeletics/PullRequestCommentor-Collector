package com.freeletcis.pullrequestcommentor.collecltor.android.lint.model

import com.tickaroo.tikxml.annotation.Element
import com.tickaroo.tikxml.annotation.Xml

@Xml
data class AndroidLintReport (
        @Element val issues : List<AndroidLintIssue>?
)