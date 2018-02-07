# Collector for [PullRequestCommentor](TODO)

This is a simple tool that allows you too collect reports from various code quality tools (like static code analysis 
or even unit test results) and to generate one `.xml` file with comments for a pull request. This `.xml` file containing
comments can then be used as input for  [PullRequestCommentor](TODO) to comment the results directly on a Pull Request.



# Configuration File
The configuration file is a `.yml` file. In this file you can specify which plugins are used and where the final result, 
the `.xml` file, will be stored. Plugins are just `.jar` files. To add a plugin to the collector you have to specify the 
path to the plugin jar. Per default, no plugin is applied. You explicitly have to add at least one plugin to make collector work.

```yaml
output: 'some/path/comments.xml'  # The file where the final output should be stored
plugins:  # A list of plugins
    
    - jar: 'path/to/example-plugin.jar'  # add example-plugin (this could also be a url)
      params:  # Parameters for example-plugin.jar. Check the specific plugin details for information about parameters
        - 1                # Parameter 1
        - "A string"       # Paramater 2
        - true             # Parameter 3
        - 2.0              # Paramater 4
        
    - jar: 'path/to/findbugs-plugin.jar' # add findbug plugins
      params: 
        - 'foo/build/findbug-results.xml'   # Parameter 1 for findbug plugin
        - 'bar/build/findbug-results.xml'   # Parameter 2 for findbug plugin
      if_no_error_comments_continue_with_plugins:  # If findbug hasn't produced error comments continue with this plugins
              - jar: 'path/to/pmd-plugin.jar' # Add pmd-plugin only if findbug hasn't produced error comments
                params: 
                  - 'foo/build/pmd-results.xml'   # Parameter 1 for pmd plugin
                  - 'bar/build/pmd-results.xml'   # Parameter 2 for pmd plugin
               - jar: 'path/to/error-prone-plugin.jar' # Add error-prone-plugin only if findbug hasn't produced error comments
                 params: 
                    - 'foo/build/errorprone-results.xml'   # Parameter 1 for error-prone plugin
                    - 'bar/build/errorprone-results.xml'   # Parameter 2 for error-prone plugin

    - jar: 'path/to/checkstyle.jar' # add checkstyle  plugin (runs in parallel with example-plugin)
      params: 
            - 'foo/build/ktlint.xml'   # Parameter 1 for klint plugin
            - 'bar/build/ktlint.xml'   # Parameter 2 for klint plugin
```

The example shown above demonstates how a `config.yml` file could look like.
You can add arbitarry many plugins to the collector by specifying the path to the `.jar` file of the plugin.
In addition each plugin may have (but doesn't necessarily have to have) parameters. That parameters are specified as a
list. Please note that only primitives like `String`, `int`, `boolean` and `double` are supported as parameters.

With `if_no_error_comments_continue_with_plugins` you can build a hierarchy of which plugins should run only if the 
previous one is not already reporting some errors. For example, let's take a look at the `findbugs-plugin` configuration:

```yaml
    - jar: 'path/to/findbugs-plugin.jar' # add findbug plugins
      params: 
        - 'foo/build/findbug-results.xml'   # Parameter 1 for findbug plugin
        - 'bar/build/findbug-results.xml'   # Parameter 2 for findbug plugin
      if_no_error_comments_continue_with_plugins:  # If findbug hasn't produced error comments continue with this plugins
              - jar: 'path/to/pmd-plugin.jar' # Add pmd-plugin only if findbug hasn't produced error comments
                params: 
                  - 'foo/build/pmd-results.xml'   # Parameter 1 for pmd plugin
                  - 'bar/build/pmd-results.xml'   # Parameter 2 for pmd plugin
               - jar: 'path/to/error-prone-plugin.jar' # Add error-prone-plugin only if findbug hasn't produced error comments
                 params: 
                    - 'foo/build/errorprone-results.xml'   # Parameter 1 for error-prone plugin
                    - 'bar/build/errorprone-results.xml'   # Parameter 2 for error-prone plugin
```

What this means is the following hierachy of plugins:
```
 - findbugs
    - pmd
    - error-prone

```

What it exactly means is the following:
1. Run findbugs-plugin
2. If findbugs-plugin has not produced any comments (so findbug has no issue found to report), 
then also run pmd-plugin and error-prone-plugin. If findbug-plugin has produced comments, skip pmd-plugin and error-prone plugin.

When is this useful? Let's say you have a potential NullPointerException in your code base that is detected by findbugs and pmd.
Then you don't want to make two comments (one by findbugs and one by pmd) for the same NullPointerException. With the 
configuration shown above, only findbugs will report the NullPointerException (but not pmd).




# Plugins

Per default, no plugin is enabled. You always have enable a plugin explicitly in the config file. 
Plugins are basically just `.jar` files.

We provide the following plugins:
 - [Android Lint](https://github.com/freeletics/PullRequestCommentor-Collector/tree/master/plugins/android/android-lint)
 - [Checkstyle](https://github.com/freeletics/PullRequestCommentor-Collector/tree/master/plugins/java/checkstyle)
    - [ktlint](https://ktlint.github.io) by using checkstyle reporter option
    - [detekt](https://github.com/arturbosch/detekt) by using checkstyle reporter option
    - [SwiftLint](https://github.com/realm/SwiftLint) by using checkstyle reporter option
    - [PHP_CodeSniffer](https://github.com/squizlabs/PHP_CodeSniffer) by using checkstyle reporter option
 
 
 ## Write your own Plugin
 
If you want to write your own plugin, please check [this section](TODO).