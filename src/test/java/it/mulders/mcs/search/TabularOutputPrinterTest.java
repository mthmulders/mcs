package it.mulders.mcs.search;

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
//    private final SearchQuery query = SearchQuery.search("org.codehaus.plexus:plexus-utils").build();

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
        output.print(SearchQuery.search("org.codehaus.plexus:plexus-utils").build(), response, new PrintStream(buffer));


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
        output.print(SearchQuery.search("org.codehaus.plexus:plexus-utils").build(), response, new PrintStream(buffer));


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
        output.print(SearchQuery.search("org.codehaus.plexus:plexus-utils").build(), response, new PrintStream(buffer));


        // Assert
        var table = buffer.toString();
        assertThat(table).contains("Found 1 results");
    }

    @Test
    void should_mention_when_number_of_results_differs_from_requested() {
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
        output.print(SearchQuery.search("org.codehaus.plexus:plexus-utils").withLimit(5).build(), response, new PrintStream(buffer));


        // Assert
        var table = buffer.toString();
        assertThat(table).contains("showing first 1");
    }
}
