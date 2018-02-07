# Checkstyle
This collector plugin collects reports from [checkstyle](http://checkstyle.sourceforge.net).
Please note that there are also some other code quality tools such as [ktlint](https://ktlint.github.io), [detekt](https://github.com/arturbosch/detekt) or [SwiftLint](https://github.com/realm/SwiftLint)
that actually generate report files in checkstyle xml format. Hence those tools are also supported out of the box by applying this plugin.

# Download
TODO

# Usage
To add this plugin to the collector add the following configuration to collector's `config.yml` file:

```yaml
plugins:
    - jar: 'path/to/checkstyle-plugin.jar'
      params:
        - 'error' # Servity level. See params section
        - 'repo-folder/' # The path prefix that should be removed from lint location for a given issue
        - '/Users/ci/workspace/some-app/build/reports' # The path to the directory where scanning should start
        -  ['checkstyle-report.xml', '*ktlint-report-in-checkstyle-format.xml.xml']  # Glob (similar to regex) to define which checkstyle result files should be read
```

## Parameters
- The first parameter is the severity of the issues you would like to report as comment. Values (as String) are `error` 
(means only report issues with severity "error"), `warning` (only report issues with severity "error" or "warning")
or `info` (only report issues incl. "error", "warning" and "information" servity) or `ignore` which basically means report all issues.
- The second parameter is the path prefix to the current working directory. Checkstyle defines paths that may or may not exactly match 
you git repository structure. However, PullRequest Commentor requires relative paths starting from the root directory of your git repository 
(i.e. "repo-folder/app/src/main/java/com/freeletics/util/DateUtil.java"). Therfore, you have to define the path prefix that must be 
removed like this: `repo-folder/` if all the "repo-folder" prefixes should be removed. If you don
t want to remove any prefix use the empty string (` '' `)
- The third parameter specifies the starting directory where you would like to start searching for files that match the provided 
[globals](https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String))
- The last parameter is a list of [glob](https://en.wikipedia.org/wiki/Glob_(programming)) that
 describe where to find the `checkstyle-report.xml` files that should be read and transformed to comments for the pull-request.
 This list must contain at least one glob.