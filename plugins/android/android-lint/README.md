# Android Lint Plugin
This collector plugin collects reports from android lint-result.xml files

# Download
TODO

# Usage
To add this plugin to the collector add the following configuration to collector's `config.yml` file:

```yaml
plugins:
    - jar: path/to/android-lint-plugin.jar
      params:
        - 'Warnings' # Servity level. See params section
        - '/Users/ci/workspace/some-app/' # The path prefix that should be removed from lint location for a given issue
        - '/Users/ci/workspace/some-app/build' # The path to the directory where scanning should start
        -  ['*/build/lint-result.xml', '*/other/dir/*/build/lint-result.xml']  # Regex to match for lint results files.
```

## Parameters
- The first paramter is the servity of the lint issues you would like to report. Values (as String) are `Error` 
(means only report lint issues with servity "Error"), `Warning` (only report issues with servity "Error" or "Warning")
or `Information` (only report all lint issues incl. "Error", "Warning" and "Information" servity)
- The second parameter is the path prefix to the current working directory. Usually lint defines a location to a lint 
issue as follows: `file="/Users/Hannes/workspace/freeletics-android/app/src/main/java/com/freeletics/util/DateUtil.java"`.
However, PullRequest Commentor requires relative paths starting from the root directory of your git repository 
(i.e. "app/src/main/java/com/freeletics/util/DateUtil.java"). Therfore, you have to define the path prefix that must be 
removed like this: `/Users/Hannes/workspace/freeletics-android/`
- The third parameter specifies the starting directory where you would like to start searching for files that match 
the [regex](https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html) or 
[global](https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String))
- The last parameter is a list of [regex](https://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html) or 
[global](https://docs.oracle.com/javase/7/docs/api/java/nio/file/FileSystem.html#getPathMatcher(java.lang.String)) that
 describe where to find the `lint-report.xml` files that should be read and transformed to comments for the pull-request.