package it.mulders.mcs.search.printer;

import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.SearchResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.stream.Stream;

class CoordinatePrinterTest implements WithAssertions {

    private static final SearchQuery QUERY = SearchQuery.search("org.codehaus.plexus:plexus-utils").build();
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
    private static final String POM_XML_OUTPUT = """
            <dependency>
                <groupId>org.codehaus.plexus</groupId>
                <artifactId>plexus-utils</artifactId>
                <version>3.4.1</version>
            </dependency>
            """;
    private static final String GRADLE_GROOVY_OUTPUT = "implementation group: 'org.codehaus.plexus', name: 'plexus-utils', version: '3.4.1'";
    private static final String GRADLE_GROOVY_SHORT_OUTPUT = "'org.codehaus.plexus:plexus-utils:3.4.1'";

    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    private static Stream<Arguments> coordinatePrinters() {
        return Stream.of(
                Arguments.of(new PomXmlOutput(), POM_XML_OUTPUT),
                Arguments.of(new GradleGroovyOutput(), GRADLE_GROOVY_OUTPUT),
                Arguments.of(new GradleGroovyShortOutput(), GRADLE_GROOVY_SHORT_OUTPUT)
        );
    }

    @ParameterizedTest
    @MethodSource("coordinatePrinters")
    void should_print_snippet(CoordinatePrinter printer, String expected) {
        printer.print(QUERY, RESPONSE, new PrintStream(buffer));
        var xml = buffer.toString();

        assertThat(xml).containsIgnoringWhitespaces(expected);
    }
}