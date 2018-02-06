package com.freeletics.pullrequestcommentor.collector.checkstyle.model

enum class CheckstyleSeverity(private val priorityComparison: Int) {
    ERROR(1),
    WARNING(2),
    INFO(3),
    IGNORE(4);


    fun isHigherOrEqualPrio(other : CheckstyleSeverity) = this.priorityComparison >= other.priorityComparison
}