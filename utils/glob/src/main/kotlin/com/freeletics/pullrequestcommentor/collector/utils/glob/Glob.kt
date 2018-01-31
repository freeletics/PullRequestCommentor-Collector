package com.freeletics.pullrequestcommentor.collector.utils.glob

/**
 * A little util class that converts a Glob string to a regex.
 * Borrowed from https://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
 * @author Neil Traft
 */
object Glob {

    /**
     * Converts a standard POSIX Shell globbing pattern into a regular expression
     * pattern. The result can be used with the standard [java.util.regex] API to
     * recognize strings which match the glob pattern.
     *
     *
     * See also, the POSIX Shell language:
     * http://pubs.opengroup.org/onlinepubs/009695399/utilities/xcu_chap02.html#tag_02_13_01
     *
     * @param pattern A glob pattern.
     * @return A regex pattern to recognize the given glob pattern.
     */
    fun convertGlobToRegex(pattern: String): Regex {
        val sb = StringBuilder(pattern.length)
        var inGroup = 0
        var inClass = 0
        var firstIndexInClass = -1
        val arr = pattern.toCharArray()
        var i = 0
        while (i < arr.size) {
            val ch = arr[i]
            when (ch) {
                '\\' -> if (++i >= arr.size) {
                    sb.append('\\')
                } else {
                    val next = arr[i]
                    when (next) {
                        ',' -> {
                        }
                        'Q', 'E' -> {
                            // extra escape needed
                            sb.append('\\')
                            sb.append('\\')
                        }
                        else -> sb.append('\\')
                    }// escape not needed
                    sb.append(next)
                }
                '*' -> if (inClass == 0)
                    sb.append(".*")
                else
                    sb.append('*')
                '?' -> if (inClass == 0)
                    sb.append('.')
                else
                    sb.append('?')
                '[' -> {
                    inClass++
                    firstIndexInClass = i + 1
                    sb.append('[')
                }
                ']' -> {
                    inClass--
                    sb.append(']')
                }
                '.', '(', ')', '+', '|', '^', '$', '@', '%' -> {
                    if (inClass == 0 || firstIndexInClass == i && ch == '^')
                        sb.append('\\')
                    sb.append(ch)
                }
                '!' -> if (firstIndexInClass == i)
                    sb.append('^')
                else
                    sb.append('!')
                '{' -> {
                    inGroup++
                    sb.append('(')
                }
                '}' -> {
                    inGroup--
                    sb.append(')')
                }
                ',' -> if (inGroup > 0)
                    sb.append('|')
                else
                    sb.append(',')
                else -> sb.append(ch)
            }
            i++
        }
        return Regex(sb.toString())
    }
}
