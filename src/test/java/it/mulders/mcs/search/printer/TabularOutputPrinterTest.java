package it.mulders.mcs.search.printer;

import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.SearchResponse;
import it.mulders.mcs.search.vulnerability.ComponentReportResponse.ComponentReport;
import it.mulders.mcs.search.vulnerability.ComponentReportResponse.ComponentReport.ComponentReportVulnerability;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabularOutputPrinterTest implements WithAssertions {
    private final TabularOutputPrinter output = new TabularOutputPrinter();
    private final SearchQuery query =
            SearchQuery.search("org.codehaus.plexus:plexus-utils").build();

    @Test
    void should_print_gav() {
        // Arrange
        var response = new SearchResponse.Response(1, 0, new SearchResponse.Response.Doc[] {
            new SearchResponse.Response.Doc(
                    "org.codehaus.plexus:plexus-utils",
                    "org.codehaus.plexus",
                    "plexus-utils",
                    null,
                    "3.4.1",
                    "jar",
                    1630022910000L)
        });
        var buffer = new ByteArrayOutputStream();

        // Act
        output.print(query, response, new PrintStream(buffer));

        // Assert
        var table = buffer.toString();
        assertThat(table).contains("org.codehaus.plexus:plexus-utils:3.4.1");
    }

    @Test
    void should_print_last_updated() {
        // Arrange
        var response = new SearchResponse.Response(1, 0, new SearchResponse.Response.Doc[] {
            new SearchResponse.Response.Doc(
                    "org.codehaus.plexus:plexus-utils",
                    "org.codehaus.plexus",
                    "plexus-utils",
                    null,
                    "3.4.1",
                    "jar",
                    1630022910000L)
        });
        var buffer = new ByteArrayOutputStream();

        // Act
        output.print(query, response, new PrintStream(buffer));

        // Assert
        var table = buffer.toString();
        var lastUpdated = DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm (zzz)")
                .format(Instant.ofEpochMilli(1630022910000L).atZone(ZoneId.systemDefault()));
        assertThat(table).contains(lastUpdated);
    }

    @Test
    void should_mention_number_of_results() {
        // Arrange
        var response = new SearchResponse.Response(1, 0, new SearchResponse.Response.Doc[] {
            new SearchResponse.Response.Doc(
                    "org.codehaus.plexus:plexus-utils",
                    "org.codehaus.plexus",
                    "plexus-utils",
                    null,
                    "3.4.1",
                    "jar",
                    1630022910000L)
        });
        var buffer = new ByteArrayOutputStream();

        // Act
        output.print(query, response, new PrintStream(buffer));

        // Assert
        var table = buffer.toString();
        assertThat(table).contains("Found 1 results");
    }

    @Test
    void should_not_mention_latest_version_when_not_present() {
        // Arrange
        var response = new SearchResponse.Response(4, 0, new SearchResponse.Response.Doc[] {
            new SearchResponse.Response.Doc(
                    "org.codehaus.plexus:plexus-utils",
                    "org.codehaus.plexus",
                    "plexus-utils",
                    null,
                    null,
                    "jar",
                    1630022910000L),
            new SearchResponse.Response.Doc(
                    "org.codehaus.plexus:plexus-archiver",
                    "org.codehaus.plexus",
                    "plexus-archiver",
                    null,
                    null,
                    "jar",
                    1630022910000L)
        });
        var buffer = new ByteArrayOutputStream();

        // Act
        var query = SearchQuery.search("org.codehaus.plexus:plexus-utils")
                .withLimit(2)
                .build();
        output.print(query, response, new PrintStream(buffer));

        // Assert
        var table = buffer.toString();
        assertThat(table).doesNotContain("null");
    }

    @Test
    void should_mention_when_number_of_results_is_larger_than_the_search_limit() {
        // Arrange
        var response = new SearchResponse.Response(4, 0, new SearchResponse.Response.Doc[] {
            new SearchResponse.Response.Doc(
                    "org.codehaus.plexus:plexus-utils",
                    "org.codehaus.plexus",
                    "plexus-utils",
                    null,
                    "3.4.1",
                    "jar",
                    1630022910000L),
            new SearchResponse.Response.Doc(
                    "org.codehaus.plexus:plexus-archiver",
                    "org.codehaus.plexus",
                    "plexus-archiver",
                    null,
                    "4.2.7",
                    "jar",
                    1630022910000L)
        });
        var buffer = new ByteArrayOutputStream();

        // Act
        var query = SearchQuery.search("org.codehaus.plexus:plexus-utils")
                .withLimit(2)
                .build();
        output.print(query, response, new PrintStream(buffer));

        // Assert
        var table = buffer.toString();
        assertThat(table).contains("showing 2");
    }

    @Test
    void should_not_mention_when_number_of_results_is_equal_to_the_search_limit() {
        // Arrange
        var response = new SearchResponse.Response(2, 0, new SearchResponse.Response.Doc[] {
            new SearchResponse.Response.Doc(
                    "org.codehaus.plexus:plexus-utils",
                    "org.codehaus.plexus",
                    "plexus-utils",
                    null,
                    "3.4.1",
                    "jar",
                    1630022910000L),
            new SearchResponse.Response.Doc(
                    "org.codehaus.plexus:plexus-archiver",
                    "org.codehaus.plexus",
                    "plexus-archiver",
                    null,
                    "4.2.7",
                    "jar",
                    1630022910000L)
        });
        var buffer = new ByteArrayOutputStream();

        // Act
        var query = SearchQuery.search("org.codehaus.plexus:plexus-utils")
                .withLimit(2)
                .build();
        output.print(query, response, new PrintStream(buffer));

        // Assert
        var table = buffer.toString();
        assertThat(table).doesNotContain("showing 1");
    }

    @Test
    void should_not_mention_when_number_of_results_is_smaller_than_the_search_limit() {
        // Arrange
        var response = new SearchResponse.Response(1, 0, new SearchResponse.Response.Doc[] {
            new SearchResponse.Response.Doc(
                    "org.codehaus.plexus:plexus-utils",
                    "org.codehaus.plexus",
                    "plexus-utils",
                    null,
                    "3.4.1",
                    "jar",
                    1630022910000L)
        });
        var buffer = new ByteArrayOutputStream();

        // Act
        var query = SearchQuery.search("org.codehaus.plexus:plexus-utils")
                .withLimit(2)
                .build();
        output.print(query, response, new PrintStream(buffer));

        // Assert
        var table = buffer.toString();
        assertThat(table).doesNotContain("showing 1");
    }

    @Test
    void should_print_vulnerability_text() {
        // Arrange
        var output = new TabularOutputPrinter(true);
        var query = SearchQuery.search("org.apache.shiro:shiro-web").build();

        var response = new SearchResponse.Response(1, 0, new SearchResponse.Response.Doc[] {
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
                            "https://ossindex.sonatype.org/component/pkg:maven/org.apache.shiro/shiro-web@1.10.0",
                            new ComponentReportVulnerability[] {
                                new ComponentReportVulnerability(
                                        "CVE-2023-34478",
                                        "CWE-22: Improper Limitation of a Pathname to a Restricted Directory ('Path Traversal')",
                                        9.8,
                                        "https://ossindex.sonatype.org/vulnerability/CVE-2023-34478?component-type=maven&component-name=org.apache.shiro%2Fshiro-web"),
                                new ComponentReportVulnerability(
                                        "CVE-2020-13933",
                                        "[CVE-2020-13933] CWE-287: Improper Authentication",
                                        7.5,
                                        "https://ossindex.sonatype.org/vulnerability/CVE-2020-13933?component-type=maven&component-name=org.apache.shiro%2Fshiro-web")
                            }))
        });

        // Act
        var buffer = new ByteArrayOutputStream();
        output.print(query, response, new PrintStream(buffer));

        // Assert
        var table = buffer.toString();
        assertThat(table).contains("Vulnerabilities");
        assertThat(table).contains("1 Critical, 1 High");
    }
}
