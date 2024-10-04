# Maven Central Search

[![Build status](https://github.com/mthmulders/mcs/actions/workflows/build.yml/badge.svg)](https://github.com/mthmulders/mcs/actions/workflows/build.yml)
[![Mutation testing badge](https://img.shields.io/endpoint?style=flat&url=https%3A%2F%2Fbadge-api.stryker-mutator.io%2Fgithub.com%2Fmthmulders%2Fmcs%2Fmain)](https://dashboard.stryker-mutator.io/reports/github.com/mthmulders/mcs/main)
[![Snapcraft.io status](https://snapcraft.io/maven-central-search/badge.svg)](https://snapcraft.io/maven-central-search)

> Use [Maven Central Repository Search](https://search.maven.org/) from your command line!

Use `mcs` to quickly lookup dependency coordinates in Maven Central, without having to switch to your browser.

## Usage

This tool supports the following modes of searching:

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

   If there are multiple hits, you will get the same table output as above.
   But if there's only one hit, this will give you by default a pom.xml snippet for the artifact you searched for.
   Ready for copy & paste in your favourite IDE!  
   If you require snippet in different format, use `-f <type>` or `--format=<type>`.
   Supported types are: `maven`, `gradle`, `gradle-short`, `gradle-kotlin`, `sbt`, `ivy`, `grape`, `leiningen`, `buildr`, `jbang`, `gav`.

3. **Class-name search**

   ```console
   mcs class-search CommandLine
   mcs class-search -f picocli.CommandLine
   ```

   This will give you all artifacts in Maven Central that contain a particular class.
   If you set the `-f` flag, the search term is considered a "fully classified" class name, so including the package name.

## Flags

* All modes recognise the `-l <number>` switch, which lets you specify how many results you want to see _at most_.
* In **Wildcard sarch** and **Coordinate search**, you can pass along the `-s` (or `--show-vulnerabilities`) flag.
  It will cause MCS to show a summary of reported security vulnerabilities against each result.
  If there is only one search result, it will display the CVE numbers reported against that result.
  **Note** that this feature will probably soon hit the API limits for the Sonatype OSS Index. See [their documentation](https://ossindex.sonatype.org) for details on how this may impact your usage.
  You can specify your credentials using the system properties `ossindex.username` and `ossindex.password`.
  See under "Configuring MCS" on how to do this in the most convenient way.

## Installation

You can install mcs using the package manager of your choice:

| Package manager | Platform |            Installation             | Remarks |
|-----------------|----------|-------------------------------------|---------|
| **Homebrew**    | 🍎 🐧    | `brew install mthmulders/tap/mcs`   | ⚠️ 1    |
| **Snap**        | 🐧       | `snap install maven-central-search` |         |
| **SDKMAN!**     | 🍎 🐧    | `sdk install mcs`                   |         |
| **Chocolatey**  | 🪟       | `choco install mcs`                 |         |
| **Scoop**       | 🪟       | `scoop install mthmulders/mcs`      |         |

1. The Linux binaries only work on x86_64 CPU's.
   There Apple binaries for both x86_64 and Apple Silicon, so you don't need Rosetta.

### Usage with custom trust store

In certain situations, such as when you work behind a TLS-intercepting (corporate) firewall, MCS may fail with

> PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target

In layman's speak: the default, built-in trust store (the set of trusted X.509 certificates) does not contain anything that allows to trust the certificate(s) presented by the server.
Maven Central uses a certificate that would've been trusted, but the culprit here is the TLS-intercepting (corporate) firewall that presents an internal certificate.

The solution is to create a trust store that has the "highest" certificate in the certificate chain, e.g. that of the (internal) certificate authority.
You can use a tool like [Portecle](https://portecle.sourceforge.net/) to create such a trust store.
Next, point MCS to that trust store like so

```
mcs -Djavax.net.ssl.trustStore=/path/to/keystore search something
```

### Usage Behind a Proxy

If you are running behind a proxy, MCS will respect the `HTTP_PROXY` and `HTTPS_PROXY` environment variables.

## Configuring MCS

Some configuration for MCS is passed through system properties.
You can do this every time you invoke MCS by adding `-Dxxx=yyy`.
To make it more conveniently, you can create a configuration file that will automatically be read by MCS and interpreted as configuration settings.

To do so, create a directory **.mcs** in your user directory (typically **C:\Users\<your-user-name>** on 🪟, **/home/<your-user-name>** on 🐧 or **/Users/<your-user-name>** on 🍎).
Inside that folder, create a file **mcs.config** and write the following line in it:

```
javax.net.ssl.trustStore=/path/to/keystore
ossindex.username=xxx
ossindex.password=yyy
```

This way, you don't have to remember passing the `-D`.

## Contributing

Probably the easiest way to get a working development environment is to use Gitpod:

[![Open in Gitpod](https://gitpod.io/button/open-in-gitpod.svg)](https://gitpod.io/#https://github.com/mthmulders/mcs)

It will configure a workspace in your browser and show that everything works as expected by running `mvn verify`.
This setup does not touch your computer - as soon as you close your browser tab, it's gone.

Checkout the [issues](https://github.com/mthmulders/mcs/issues) if you're looking for something to work on.
If you have a new idea, feel free to bring it up using the [discussions](https://github.com/mthmulders/mcs/discussions).

## Acknowledgements
MCS would not have been possible without the contributions of wonderful people around the globe.
The full list is in [CONTRIBUTORS.md](./CONTRIBUTORS.md).
