package com.freeletics.pullrequestcommentor.collector

/**
 * Each collector must implement this plugin
 */
interface CollectorPlugin {

    /**
     * Get the list of comments that are produced by this Collector Plugin.
     * If the plugin has no comment to create this method should return an empty list.
     */
    fun getComments(): PluginResult
}