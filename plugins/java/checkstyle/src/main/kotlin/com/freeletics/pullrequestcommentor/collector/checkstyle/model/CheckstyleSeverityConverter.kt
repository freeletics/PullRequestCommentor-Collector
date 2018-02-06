package com.freeletics.pullrequestcommentor.collector.checkstyle.model

import com.tickaroo.tikxml.TypeConverter

/**
 * A Type Converter for [CheckstyleSeverity]
 */
class CheckstyleSeverityConverter : TypeConverter<CheckstyleSeverity> {

    override fun write(value: CheckstyleSeverity?): String? = if (value == null) null else when (value) {
        CheckstyleSeverity.ERROR -> "error"
        CheckstyleSeverity.INFO -> "info"
        CheckstyleSeverity.WARNING -> "warning"
         CheckstyleSeverity.IGNORE -> "ignore"
    }

    override fun read(value: String?): CheckstyleSeverity? = if (value == null) null else when (value) {
        "error" -> CheckstyleSeverity.ERROR
        "info" -> CheckstyleSeverity.INFO
        "warning" -> CheckstyleSeverity.WARNING
        "ignore" -> CheckstyleSeverity.IGNORE
        else -> throw IllegalArgumentException("CheckstyleSeverity with name '$value' is not supported")
    }
}