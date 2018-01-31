package com.freeletics.pullrequestcommentor.collector

class TestCollectorWithParams(
        i: Int,
        str: String,
        b: Boolean,
        d: Double
) : CollectorPlugin {

    companion object {
        var anInt: Int = 0
        var aString: String = "aString"
        var aBoolean: Boolean = false
        var aDouble: Double = 0.0
        lateinit var result: PluginResult
    }

    init {
        anInt = i
        aString = str
        aBoolean = b
        aDouble = d
    }


    override fun getComments(): PluginResult = result
}