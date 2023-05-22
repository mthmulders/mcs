package it.mulders.mcs.search.printer;

import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.SearchResponse;
import it.mulders.mcs.search.printer.clipboard.Clipboard;
import it.mulders.mcs.search.printer.clipboard.CopyToClipboardConfig;
import it.mulders.mcs.search.printer.clipboard.CopyToClipboardPrinter;
import it.mulders.mcs.search.printer.clipboard.SystemClipboard;

import java.io.PrintStream;

public sealed interface CoordinatePrinter extends OutputPrinter, CopyToClipboardPrinter
        permits BuildrOutput, GradleGroovyOutput, GradleGroovyShortOutput, GradleKotlinOutput, GrapeOutput,
        IvyXmlOutput, LeiningenOutput, PomXmlOutput, SbtOutput {

    String provideCoordinates(final String group, final String artifact, final String version);

    @Override
    default void print(final SearchQuery query, final SearchResponse.Response response, final PrintStream stream,
               final CopyToClipboardConfig copyToClipboardConfig) {
        if (response.numFound() != 1) {
            throw new IllegalArgumentException("Search response with more than one result not expected here");
        }

        var doc = response.docs()[0];
        String coordinates = provideCoordinates(doc.g(), doc.a(), first(doc.v(), doc.latestVersion()));

        stream.println();
        stream.println(coordinates);
        stream.println();

        if (copyToClipboardConfig.copyToClipboardEnabled()) {
            copyToClipboard(stream, new SystemClipboard(), coordinates);
        } else {
            printCopyToClipboardHint(stream, copyToClipboardConfig);
        }

        stream.println();
    }

    private static void copyToClipboard(PrintStream stream, Clipboard clipboard, String coordinates) {
        clipboard.copy(coordinates);
        stream.println("Snippet copied to clipboard.");
    }

    private static void printCopyToClipboardHint(PrintStream stream,
                                                 CopyToClipboardConfig copyToClipboardConfig) {
        stream.printf("(To directly copy this snippet to the clipboard, run mcs with the %s or %s flag.)%n",
                copyToClipboardConfig.shortFlagName(),
                copyToClipboardConfig.longFlagName());
    }

    private String first(final String... values) {
        for (var value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
