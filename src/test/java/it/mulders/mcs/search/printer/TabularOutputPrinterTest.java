package it.mulders.mcs.search.printer;

import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.SearchResponse;
import it.mulders.mcs.search.printer.clipboard.CopyToClipboardConfiguration;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabularOutputPrinterTest implements WithAssertions {
    private final TabularOutputPrinter output = new TabularOutputPrinter();
    private final SearchQuery query = SearchQuery.search("org.codehaus.plexus:plexus-utils").build();
    private final CopyToClipboardConfiguration dontCopyToClipboard = new CopyToClipboardConfiguration(
            "-topt", "--tabular-output-printer-test", false);

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
                        1630022910000L
                )
        });
        var buffer = new ByteArrayOutputStream();


        // Act
        output.print(query, response, new PrintStream(buffer), dontCopyToClipboard);


        // Assert
        var table = buffer.toString();
        assertThat(table).contains("org.codehaus.plexus:plexus-utils");
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
                        1630022910000L
                )
        });
        var buffer = new ByteArrayOutputStream();


        // Act
        output.print(query, response, new PrintStream(buffer), dontCopyToClipboard);


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
                        1630022910000L
                )
        });
        var buffer = new ByteArrayOutputStream();


        // Act
        output.print(query, response, new PrintStream(buffer), dontCopyToClipboard);


        // Assert
        var table = buffer.toString();
        assertThat(table).contains("Found 1 results");
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
                        1630022910000L
                ),
                new SearchResponse.Response.Doc(
                        "org.codehaus.plexus:plexus-archiver",
                        "org.codehaus.plexus",
                        "plexus-archiver",
                        null,
                        "4.2.7",
                        "jar",
                        1630022910000L
                )
        });
        var buffer = new ByteArrayOutputStream();


        // Act
        var query = SearchQuery.search("org.codehaus.plexus:plexus-utils").withLimit(2).build();
        output.print(query, response, new PrintStream(buffer), dontCopyToClipboard);


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
                        1630022910000L
                ),
                new SearchResponse.Response.Doc(
                        "org.codehaus.plexus:plexus-archiver",
                        "org.codehaus.plexus",
                        "plexus-archiver",
                        null,
                        "4.2.7",
                        "jar",
                        1630022910000L
                )
        });
        var buffer = new ByteArrayOutputStream();


        // Act
        var query = SearchQuery.search("org.codehaus.plexus:plexus-utils").withLimit(2).build();
        output.print(query, response, new PrintStream(buffer), dontCopyToClipboard);


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
                        1630022910000L
                )
        });
        var buffer = new ByteArrayOutputStream();


        // Act
        var query = SearchQuery.search("org.codehaus.plexus:plexus-utils").withLimit(2).build();
        output.print(query, response, new PrintStream(buffer), dontCopyToClipboard);


        // Assert
        var table = buffer.toString();
        assertThat(table).doesNotContain("showing 1");
    }
}
