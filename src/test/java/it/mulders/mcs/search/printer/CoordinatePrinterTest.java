package it.mulders.mcs.search.printer;

import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.SearchResponse;
import it.mulders.mcs.search.printer.clipboard.Clipboard;
import it.mulders.mcs.search.printer.clipboard.CopyToClipboardConfig;
import it.mulders.mcs.search.printer.clipboard.SystemClipboard;
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
    private final Clipboard clipboard = new SystemClipboard();

    private static Stream<Arguments> coordinatePrinters() {
        return Stream.of(
                Arguments.of(new PomXmlOutput(), POM_XML_OUTPUT),
                Arguments.of(new GradleGroovyOutput(), GRADLE_GROOVY_OUTPUT),
                Arguments.of(new GradleGroovyShortOutput(), GRADLE_GROOVY_SHORT_OUTPUT),
                Arguments.of(new GradleKotlinOutput(), GRADLE_KOTLIN_OUTPUT),
                Arguments.of(new SbtOutput(), SBT_OUTPUT),
                Arguments.of(new IvyXmlOutput(), IVY_XML_OUTPUT),
                Arguments.of(new GrapeOutput(), GRAPE_OUTPUT),
                Arguments.of(new LeiningenOutput(), LEININGEN_OUTPUT),
                Arguments.of(new BuildrOutput(), BUILDR_OUTPUT)
        );
    }

    @ParameterizedTest
    @MethodSource("coordinatePrinters")
    void should_print_snippet(CoordinatePrinter printer, String expected) {
        var dontCopyToClipboard = new CopyToClipboardConfig(
                "-cpt", "--coordinate-printer-test", false);
        printer.print(QUERY, RESPONSE, new PrintStream(buffer), dontCopyToClipboard);
        var xml = buffer.toString();

        assertThat(xml).containsIgnoringWhitespaces(expected);
    }

    @ParameterizedTest
    @MethodSource("coordinatePrinters")
    void should_print_clipboard_hint(CoordinatePrinter printer, String expected) {
        var dontCopyToClipboard = new CopyToClipboardConfig(
                "-cpt", "--coordinate-printer-test", false);
        printer.print(QUERY, RESPONSE, new PrintStream(buffer), dontCopyToClipboard);
        var output = buffer.toString();

        assertThat(output).containsIgnoringWhitespaces("To directly copy this snippet to the clipboard");
        assertThat(output).containsIgnoringWhitespaces("-cpt");
        assertThat(output).containsIgnoringWhitespaces("--coordinate-printer-test");

        assertThat(output).doesNotContain("Snippet copied to clipboard.");
    }

    @ParameterizedTest
    @MethodSource("coordinatePrinters")
    void should_copy_to_clipboard_and_not_print_clipboard_hint(CoordinatePrinter printer, String expected) {
        var copyToClipboard = new CopyToClipboardConfig(
                "-cpt", "--coordinate-printer-test", true);
        printer.print(QUERY, RESPONSE, new PrintStream(buffer), copyToClipboard);
        var output = buffer.toString();

        assertThat(clipboard.paste()).isEqualToIgnoringWhitespace(expected);

        assertThat(output).contains("Snippet copied to clipboard.");
        assertThat(output).doesNotContain("To directly copy this snippet to the clipboard");
    }
}