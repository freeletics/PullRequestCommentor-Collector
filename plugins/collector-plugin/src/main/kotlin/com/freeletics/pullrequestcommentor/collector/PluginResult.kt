package com.freeletics.pullrequestcommentor.collector

import sun.plugin2.main.server.Plugin

/**
 * Results for
 */
sealed class PluginResult {

    /**
     * Means no Comment is produced by this plugin
     */
    object NoComment : PluginResult()

    /**
     * In case that the plugin has no error comments to report as a pull request comment
     */
    data class SuccessComment(val comment: SimpleComment) : PluginResult()

    /**
     * In case that the plugin has error comments to report, here is a list with all errors comments to report
     */
    data class ErrorComments(val errorComments: List<Comment>) : PluginResult()

}