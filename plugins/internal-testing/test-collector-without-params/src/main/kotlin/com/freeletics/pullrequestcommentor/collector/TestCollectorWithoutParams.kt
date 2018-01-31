package com.freeletics.pullrequestcommentor.collector

class TestCollectorWithoutParams : CollectorPlugin {

    companion object {
        lateinit var result: PluginResult
    }

    override fun getComments(): PluginResult = result
}