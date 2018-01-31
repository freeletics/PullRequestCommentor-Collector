package com.freeletics.pullrequest.commentor.collector.utils.glob

import com.freeletics.pullrequestcommentor.collector.utils.glob.Glob
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Borrowed from https://stackoverflow.com/questions/1247772/is-there-an-equivalent-of-java-util-regex-for-glob-type-patterns
 * @author Neil Traft
 */
class Glob_ConvertGlobToRegex_Test {

    @Test
    @Throws(Exception::class)
    fun star_becomes_dot_star() {
        assertEquals("gl.*b", Glob.convertGlobToRegex("gl*b").pattern)
    }

    @Test
    @Throws(Exception::class)
    fun escaped_star_is_unchanged() {
        assertEquals("gl\\*b", Glob.convertGlobToRegex("gl\\*b").pattern)
    }

    @Test
    @Throws(Exception::class)
    fun question_mark_becomes_dot() {
        assertEquals("gl.b", Glob.convertGlobToRegex("gl?b").pattern)
    }

    @Test
    @Throws(Exception::class)
    fun escaped_question_mark_is_unchanged() {
        assertEquals("gl\\?b", Glob.convertGlobToRegex("gl\\?b").pattern)
    }

    @Test
    @Throws(Exception::class)
    fun character_classes_dont_need_conversion() {
        assertEquals("gl[-o]b", Glob.convertGlobToRegex("gl[-o]b").pattern)
    }

    @Test
    @Throws(Exception::class)
    fun escaped_classes_are_unchanged() {
        assertEquals("gl\\[-o\\]b", Glob.convertGlobToRegex("gl\\[-o\\]b").pattern)
    }

    @Test
    @Throws(Exception::class)
    fun negation_in_character_classes() {
        assertEquals("gl[^a-n!p-z]b", Glob.convertGlobToRegex("gl[!a-n!p-z]b").pattern)
    }

    @Test
    @Throws(Exception::class)
    fun nested_negation_in_character_classes() {
        assertEquals("gl[[^a-n]!p-z]b", Glob.convertGlobToRegex("gl[[!a-n]!p-z]b").pattern)
    }

    @Test
    @Throws(Exception::class)
    fun escape_carat_if_it_is_the_first_char_in_a_character_class() {
        assertEquals("gl[\\^o]b", Glob.convertGlobToRegex("gl[^o]b").pattern)
    }

    @Test
    @Throws(Exception::class)
    fun metachars_are_escaped() {
        assertEquals("gl..*\\.\\(\\)\\+\\|\\^\\$\\@\\%b", Glob.convertGlobToRegex("gl?*.()+|^$@%b").pattern)
    }

    @Test
    @Throws(Exception::class)
    fun metachars_in_character_classes_dont_need_escaping() {
        assertEquals("gl[?*.()+|^$@%]b", Glob.convertGlobToRegex("gl[?*.()+|^$@%]b").pattern)
    }

    @Test
    @Throws(Exception::class)
    fun escaped_backslash_is_unchanged() {
        assertEquals("gl\\\\b", Glob.convertGlobToRegex("gl\\\\b").pattern)
    }

    @Test
    @Throws(Exception::class)
    fun slashQ_and_slashE_are_escaped() {
        assertEquals("\\\\Qglob\\\\E", Glob.convertGlobToRegex("\\Qglob\\E").pattern)
    }

    @Test
    @Throws(Exception::class)
    fun braces_are_turned_into_groups() {
        assertEquals("(glob|regex)", Glob.convertGlobToRegex("{glob,regex}").pattern)
    }

    @Test
    @Throws(Exception::class)
    fun escaped_braces_are_unchanged() {
        assertEquals("\\{glob\\}", Glob.convertGlobToRegex("\\{glob\\}").pattern)
    }

    @Test
    @Throws(Exception::class)
    fun commas_dont_need_escaping() {
        assertEquals("(glob,regex),", Glob.convertGlobToRegex("{glob\\,regex},").pattern)
    }
}