package com.freeletcis.pullrequestcommentor.collecltor.android.lint

/**
 * Specify the level of servity that should be reported as comments
 */
enum class AndroidLintServity {
    /**
     * Only report Errors as comments
     */
    Error,
    /**
     * Report Errors and Warnings as comments
     */
    Warning,
    /**
     * Report Errors, Warnings and Information as comments
     */
    Information
}
