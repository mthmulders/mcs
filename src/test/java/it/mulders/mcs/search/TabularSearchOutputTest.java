package it.mulders.mcs.search;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class TabularSearchOutputTest implements WithAssertions {
    private final TabularSearchOutput output = new TabularSearchOutput();

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
        output.print(response, new PrintStream(buffer));


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
        output.print(response, new PrintStream(buffer));


        // Assert
        var table = buffer.toString();
        assertThat(table).contains("27 Aug 2021 at 02:08 (CEST)");
    }
}