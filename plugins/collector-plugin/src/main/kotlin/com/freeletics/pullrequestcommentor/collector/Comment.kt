package com.freeletics.pullrequestcommentor.collector

sealed class Comment


/**
 * A simple comment that is shown in the pull requests "conversation" section.
 */
data class SimpleComment(
        /**
         * The text that should be posted as a simple comment on the pull request "conversation" section.
         * Markdown is supported.
         */
        val commentText: String
) : Comment()


/**
 * A comment on a file at the given line number
 */
data class FileLineComment(
        /**
         * The text that should be posted.
         * Markdown is supported.
         */
        val commentText: String,
        /**
         * The path in the repository to the file where we put the test
         */
        val filePath: String,

        /**
         * The line number where to put this comment
         */
        val lineNumber: Int
) : Comment()