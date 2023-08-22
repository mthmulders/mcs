package it.mulders.mcs.search.printer;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;

import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.SearchResponse;
import it.mulders.mcs.search.vulnerability.ComponentReportResponse.ComponentReport;
import it.mulders.mcs.search.vulnerability.ComponentReportResponse.ComponentReport.ComponentReportVulnerability;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

class CoordinatePrinterTest implements WithAssertions {

    private static final SearchQuery QUERY = SearchQuery.search("org.codehaus.plexus:plexus-utils").build();
    private static final SearchResponse.Response PLUGIN_RESPONSE = new SearchResponse.Response(1, 0, new SearchResponse.Response.Doc[]{
        new SearchResponse.Response.Doc(
            "org.apache.maven.plugins:maven-jar-plugin:3.3.0",
            "org.apache.maven.plugins",
            "maven-jar-plugin",
            "3.3.0",
            null,
            "maven-plugin",
            1630022910000L
        )
    });
    private static final SearchResponse.Response RESPONSE =
            new SearchResponse.Response(1, 0, new SearchResponse.Response.Doc[]{
                    new SearchResponse.Response.Doc(
                            "org.codehaus.plexus:plexus-utils:3.4.1",
                            "org.codehaus.plexus",
                            "plexus-utils",
                            "3.4.1",
                            null,
                            "jar",
                            1630022910000L
                    )
            });
    private static final String POM_XML_DEPENDENCY_OUTPUT = """
            <dependency>
                <groupId>org.codehaus.plexus</groupId>
                <artifactId>plexus-utils</artifactId>
                <version>3.4.1</version>
            </dependency>
            """;
    private static final String POM_XML_PLUGIN_OUTPUT = """
            <plugin>
                <groupId>org.apache.maven.plugins</groupId>
                <artifactId>maven-jar-plugin</artifactId>
                <version>3.3.0</version>
            </plugin>
            """;
    private static final String GRADLE_GROOVY_OUTPUT = "implementation group: 'org.codehaus.plexus', name: 'plexus-utils', version: '3.4.1'";
    private static final String GRADLE_GROOVY_SHORT_OUTPUT = "implementation 'org.codehaus.plexus:plexus-utils:3.4.1'";
    private static final String GRADLE_KOTLIN_OUTPUT = "implementation(\"org.codehaus.plexus:plexus-utils:3.4.1\")";
    private static final String SBT_OUTPUT = """
            libraryDependencies += "org.codehaus.plexus" % "plexus-utils" % "3.4.1"
            """;
    private static final String IVY_XML_OUTPUT = """
            <dependency org="org.codehaus.plexus" name="plexus-utils" rev="3.4.1"/>
            """;
    private static final String GRAPE_OUTPUT = """
            @Grapes(
                @Grab(group='org.codehaus.plexus', module='plexus-utils', version='3.4.1')
            )
            """;
    private static final String LEININGEN_OUTPUT = "[org.codehaus.plexus/plexus-utils \"3.4.1\"]";
    private static final String BUILDR_OUTPUT = "'org.codehaus.plexus:plexus-utils:jar:3.4.1'";

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private static Stream<Arguments> coordinatePrinters() {
        return Stream.of(
                Arguments.of(new PomXmlOutput(), POM_XML_DEPENDENCY_OUTPUT, RESPONSE),
                Arguments.of(new PomXmlOutput(), POM_XML_PLUGIN_OUTPUT, PLUGIN_RESPONSE),
                Arguments.of(new GradleGroovyOutput(), GRADLE_GROOVY_OUTPUT, RESPONSE),
                Arguments.of(new GradleGroovyShortOutput(), GRADLE_GROOVY_SHORT_OUTPUT, RESPONSE),
                Arguments.of(new GradleKotlinOutput(), GRADLE_KOTLIN_OUTPUT, RESPONSE),
                Arguments.of(new SbtOutput(), SBT_OUTPUT, RESPONSE),
                Arguments.of(new IvyXmlOutput(), IVY_XML_OUTPUT, RESPONSE),
                Arguments.of(new GrapeOutput(), GRAPE_OUTPUT, RESPONSE),
                Arguments.of(new LeiningenOutput(), LEININGEN_OUTPUT, RESPONSE),
                Arguments.of(new BuildrOutput(), BUILDR_OUTPUT, RESPONSE)
        );
    }

    @ParameterizedTest
    @MethodSource("coordinatePrinters")
    void should_print_snippet(CoordinatePrinter printer, String expected, SearchResponse.Response response) {
        printer.print(QUERY, response, new PrintStream(buffer));
        var xml = buffer.toString();

        assertThat(xml).isEqualToIgnoringWhitespace(expected);
    }


    @Nested
    @DisplayName("CoordinatePrinter with one vulnerabilities")
    class CoordinatePrinterWithOneVulnerabilityTest {
        private static final SearchQuery QUERY_DEPENDENCY_WITH_ONE_VULNERABILITY = SearchQuery.search("org.apache.shiro:shiro-web").build();
        private static final SearchResponse.Response RESPONSE_WITH_ONE_VULNERABILITY =
            new SearchResponse.Response(1, 0, new SearchResponse.Response.Doc[]{
                new SearchResponse.Response.Doc(
                    "org.apache.shiro:shiro-web:1.10.0",
                    "org.apache.shiro",
                    "shiro-web",
                    "1.10.0",
                    null,
                    "jar",
                    1630022910000L,
                    new ComponentReport(
                        "pkg:maven/org.apache.shiro/shiro-web@1.10.0",
                        "https://ossindex.sonatype.org/component/pkg:maven/org.apache.shiro/shiro-web@1.10.0?utm_source=postmanruntime&utm_medium=integration&utm_content=7.32.3",
                        new ComponentReportVulnerability[] {
                            new ComponentReportVulnerability("CVE-2023-34478", "CWE-22: Improper Limitation of a Pathname to a Restricted Directory ('Path Traversal')", 9.8)
                        }
                    )
                )
            });
        private static final String POM_XML_DEPENDENCY_OUTPUT_WITH_ONE_VULNERABILITY = """
            <dependency>
                <groupId>org.apache.shiro</groupId>
                <artifactId>shiro-web</artifactId>
                <version>1.10.0</version>
            </dependency>
            
            Found 1 vulnerability
            """;

        @Test
        void should_print_vulnerability_text() {
            CoordinatePrinter printer = new PomXmlOutput();
            printer.print(QUERY_DEPENDENCY_WITH_ONE_VULNERABILITY, RESPONSE_WITH_ONE_VULNERABILITY, new PrintStream(buffer));
            var xml = buffer.toString();
            assertThat(xml).isEqualToIgnoringWhitespace(POM_XML_DEPENDENCY_OUTPUT_WITH_ONE_VULNERABILITY);
        }
    }

    @Nested
    @DisplayName("CoordinatePrinter with multiple vulnerabilities")
    class CoordinatePrinterWithMultipleVulnerabilitiesTest {
        private static final SearchQuery QUERY_DEPENDENCY_WITH_MULTIPLE_VULNERABILITIES = SearchQuery.search("org.apache.shiro:shiro-web").build();
        private static final SearchResponse.Response RESPONSE_WITH_MULTIPLE_VULNERABILITIES =
            new SearchResponse.Response(1, 0, new SearchResponse.Response.Doc[]{
                new SearchResponse.Response.Doc(
                    "org.apache.shiro:shiro-web:1.9.0",
                    "org.apache.shiro",
                    "shiro-web",
                    "1.9.0",
                    null,
                    "jar",
                    1630022910000L,
                    new ComponentReport(
                        "pkg:maven/org.apache.shiro/shiro-web@1.9.0",
                        "https://ossindex.sonatype.org/component/pkg:maven/org.apache.shiro/shiro-web@1.9.0?utm_source=postmanruntime&utm_medium=integration&utm_content=7.32.3",
                        new ComponentReportVulnerability[] {
                            new ComponentReportVulnerability("CVE-2023-34478", "CWE-22: Improper Limitation of a Pathname to a Restricted Directory ('Path Traversal')", 9.8),
                            new ComponentReportVulnerability("CVE-2022-40664", "CWE-287: Improper Authentication", 9.8)
                        }
                    )
                )
            });
        private static final String POM_XML_DEPENDENCY_OUTPUT_WITH_MULTIPLE_VULNERABILITY = """
            <dependency>
                <groupId>org.apache.shiro</groupId>
                <artifactId>shiro-web</artifactId>
                <version>1.9.0</version>
            </dependency>
            
            Found 2 vulnerabilities
            """;

        @Test
        void should_print_vulnerability_text() {
            CoordinatePrinter printer = new PomXmlOutput();
            printer.print(QUERY_DEPENDENCY_WITH_MULTIPLE_VULNERABILITIES, RESPONSE_WITH_MULTIPLE_VULNERABILITIES, new PrintStream(buffer));
            var xml = buffer.toString();
            assertThat(xml).isEqualToIgnoringWhitespace(POM_XML_DEPENDENCY_OUTPUT_WITH_MULTIPLE_VULNERABILITY);
        }
    }
}