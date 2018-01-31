package com.freeletics.pullrequestcommentor.collector.model

import com.fasterxml.jackson.annotation.JsonProperty
import java.util.*

data class PluginToLoad(
        val jar: String,
        val params: Array<Any>?,
        @JsonProperty("if_no_error_comments_continue_with_plugins") val pluginsToRunIfNoErrorCommentsFromThisPlugin: List<PluginToLoad>?
) {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is PluginToLoad) return false

        if (jar != other.jar) return false
        if (!Arrays.equals(params, other.params)) return false
        if (pluginsToRunIfNoErrorCommentsFromThisPlugin != other.pluginsToRunIfNoErrorCommentsFromThisPlugin) return false

        return true
    }

    override fun hashCode(): Int {
        var result = jar.hashCode()
        result = 31 * result + (params?.let { Arrays.hashCode(it) } ?: 0)
        result = 31 * result + (pluginsToRunIfNoErrorCommentsFromThisPlugin?.hashCode() ?: 0)
        return result
    }
}