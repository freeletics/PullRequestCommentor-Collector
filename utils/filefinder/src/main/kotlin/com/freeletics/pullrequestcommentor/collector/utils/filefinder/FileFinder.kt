package com.freeletics.pullrequestcommentor.collector.utils.filefinder

import com.freeletics.pullrequestcommentor.collector.utils.glob.Glob
import java.io.IOException
import java.nio.file.FileVisitResult
import java.nio.file.FileVisitResult.CONTINUE
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.SimpleFileVisitor
import java.nio.file.attribute.BasicFileAttributes


/**
 * This class is not thread safe and should only be used once.
 * Actually, to ensure no missusage and threading issues, walking the file tree and checking for matches
 * already takes place in the constructor. At the end you only grab the result via [matchingFiles]
 */
class FileFinder private constructor(
        startingDir: Path,
        private val matcher: List<Regex>,
        private val debug: Boolean = false
) : SimpleFileVisitor<Path>() {


    private val matchingFiles = ArrayList<Path>()

    init {
        if (matcher.isEmpty()) {
            throw IllegalArgumentException("The list of matchers (globs / regex) must not be empty")
        }

        if (debug) {
            println("Regex's to search files:")
            matcher.forEachIndexed { i, regex -> println("${i + 1}.   $regex") }
        }
        Files.walkFileTree(startingDir, this)
    }


    // Compares the glob patterns against
    // the file or directory name.
    private fun find(file: Path) {
        val matches = matchesPath(file)
        if (debug) {
            println("Matches $matches - File $file (absolute ${file.toAbsolutePath()})")
        }
        if (matches) {
            matchingFiles.add(file)
        }
    }


    /**
     * Returns true if the path matches at least one matcher
     */
    private fun matchesPath(name: Path) = matcher.firstOrNull { matcher ->
        val pathStr = name.toString()
        val matches = pathStr.matches(matcher)
        matches
    } != null

    // Invoke the patterns matching
    // method on each file.
    override fun visitFile(file: Path, attrs: BasicFileAttributes): FileVisitResult {
        find(file)
        return CONTINUE
    }

    // Invoke the patterns matching
    // method on each directory.
    override fun preVisitDirectory(dir: Path, attrs: BasicFileAttributes): FileVisitResult {
        find(dir)
        return CONTINUE
    }

    override fun visitFileFailed(file: Path, exc: IOException?): FileVisitResult {
        exc?.printStackTrace()
        return CONTINUE
    }


    companion object {
        /**
         * Finds a list of files (as Paths) that are matching the given glob patterns
         *
         * @param startingDir Where the search for files matching the given path should start
         * @param globs The glob definition that must match
         * @param debug true to log the paths / files that are scanned. Default values is false.
         */
        fun find(startingDir: Path, globs: List<String>, debug: Boolean = false): List<Path> {
            val finder = FileFinder(
                    startingDir = startingDir,
                    matcher = globs.map(Glob::convertGlobToRegex),
                    debug = debug
            )

            return finder.matchingFiles
        }
    }

}