package com.freeletics.pullrequestcommentor.collector.utils

import java.io.ByteArrayOutputStream
import java.nio.charset.Charset
import java.nio.charset.StandardCharsets

class StringOutputStream(private val charset: Charset = StandardCharsets.UTF_8) : ByteArrayOutputStream() {

    fun asString() = String(toByteArray(), charset);
}