package it.mulders.mcs.search;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class PomXmlOutputTest implements WithAssertions {
    private final PomXmlOutput output = new PomXmlOutput();
    private final SearchQuery query = new CoordinateQuery("org.codehaus.plexus", "plexus-utils");

    @Test
    void should_print_pom_snippet() {
        // Arrange
        var response = new SearchResponse.Response(1, 0, new SearchResponse.Response.Doc[] {
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
        var buffer = new ByteArrayOutputStream();

        // Act
        output.print(query, response, new PrintStream(buffer));


        // Assert
        var xml = buffer.toString();
        assertThat(xml).contains("""
                
                    <dependency>
                        <groupId>org.codehaus.plexus</groupId>
                        <artifactId>plexus-utils</artifactId>
                        <version>3.4.1</version>
                    </dependency>

                """);
    }
}