package com.freeletics.pullrequestcommentor.collector.model

/**
 * Simple datastructure that represents what should be printed on the output
 */
internal sealed class Output {
    /**
     * This class represents the error case
     */
    internal data class Error(val errorMessage: String) : Output()

    /**
     * This class represents the successful case
     */
    internal data class Successful(val msg: String) : Output()
}