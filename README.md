# Maven Central Search
[![Build status](https://github.com/mthmulders/mcs/actions/workflows/build.yml/badge.svg)](https://github.com/mthmulders/mcs/actions/workflows/build.yml)
[![Mutation testing badge](https://img.shields.io/endpoint?style=flat&url=https%3A%2F%2Fbadge-api.stryker-mutator.io%2Fgithub.com%2Fmthmulders%2Fmcs%2Fmain)](https://dashboard.stryker-mutator.io/reports/github.com/mthmulders/mcs/main)

> Use [Maven Central Repository Search](https://search.maven.org/) from your command line!

Use `mcs` to quickly lookup dependency coordinates in Maven Central, without having to switch to your browser.

## Usage
This tool supports two modes of searching:

1. **Wildcard search**
    ```console
    mcs search plexus-utils
    ```
    This will give you all artifacts in Maven Central that have "plexus-utils" in their name.
    The output is in a tabular form, showing the exact coordinate of each artifact and the moment when its latest version was deployed.
2. **Coordinate search**
    ```console
   mcs search org.codehaus.plexus:plexus-utils
   mcs search org.codehaus.plexus:plexus-utils:3.4.1
    ```
   This will give you a pom.xml snippet for the artifact you searched for.
   Ready for copy & paste in your favourite IDE.

## Ideas for future development
* [ ] Proper support for multiple classifiers at a particular coordinate.
* [ ] Immediately copy the pom.xml snippet to the clipboard.
* [ ] Show the coordinates in a different form (Ivy, Gradle, SBT).
* [ ] Show a list of all versions for a given coordinate (using `--list-all`? or using a new command `list`?)

