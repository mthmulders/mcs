package it.mulders.mcs.printer;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import it.mulders.mcs.clipboard.Clipboard;
import it.mulders.mcs.search.artifact.SearchQuery;
import it.mulders.mcs.search.artifact.SearchResponse;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ClipboardOutputPrinterTest implements WithAssertions {

    private static final SearchQuery QUERY =
            SearchQuery.search("org.codehaus.plexus:plexus-utils:3.4.1").build();

    private static final SearchResponse.Response RESPONSE =
            new SearchResponse.Response(1, 0, new SearchResponse.Response.Doc[] {
                new SearchResponse.Response.Doc(
                        "org.codehaus.plexus:plexus-utils:3.4.1",
                        "org.codehaus.plexus",
                        "plexus-utils",
                        "3.4.1",
                        null,
                        "jar",
                        1630022910000L)
            });

    private final Clipboard clipboard = mock(Clipboard.class);
    private final CoordinatePrinter delegate = new GavOutput();
    private final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    @Test
    void prints_hint_when_copy_flag_is_false() {
        // Arrange
        var printer = new ClipboardOutputPrinter(delegate, clipboard, false);

        // Act
        printer.print(QUERY, RESPONSE, new PrintStream(buffer));

        // Assert
        verifyNoInteractions(clipboard);
        assertThat(buffer.toString()).contains("Pass -c or --copy to copy this snippet to your clipboard");
    }

    @Test
    void reports_failure_when_copy_fails() {
        // Arrange
        when(clipboard.copy(any())).thenReturn(false);
        var printer = new ClipboardOutputPrinter(delegate, clipboard, true);

        // Act
        printer.print(QUERY, RESPONSE, new PrintStream(buffer));

        // Assert
        assertThat(buffer.toString()).contains("Could not copy to clipboard");
    }

    @Test
    void copies_coordinates_and_prints_confirmation() {
        // Arrange
        when(clipboard.copy(any())).thenReturn(true);
        var printer = new ClipboardOutputPrinter(delegate, clipboard, true);

        // Act
        printer.print(QUERY, RESPONSE, new PrintStream(buffer));

        // Assert
        verify(clipboard).copy("org.codehaus.plexus:plexus-utils:3.4.1");
        assertThat(buffer.toString()).contains("Snippet copied to clipboard");
    }
}
