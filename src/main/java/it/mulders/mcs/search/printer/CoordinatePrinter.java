package it.mulders.mcs.search.printer;

import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.SearchResponse;
import it.mulders.mcs.search.printer.clipboard.Clipboard;
import it.mulders.mcs.search.printer.clipboard.SystemClipboard;

import java.io.PrintStream;

public sealed interface CoordinatePrinter extends OutputPrinter
        permits BuildrOutput, GradleGroovyOutput, GradleGroovyShortOutput, GradleKotlinOutput, GrapeOutput,
        IvyXmlOutput, LeiningenOutput, PomXmlOutput, SbtOutput {

    String provideCoordinates(final String group, final String artifact, final String version);

    @Override
    default void print(final SearchQuery query, final SearchResponse.Response response, final PrintStream stream) {
        if (response.numFound() != 1) {
            throw new IllegalArgumentException("Search response with more than one result not expected here");
        }

        var doc = response.docs()[0];
        String coordinates = provideCoordinates(doc.g(), doc.a(), first(doc.v(), doc.latestVersion()));

        stream.println();
        stream.println(coordinates);
        stream.println();

        copyToClipboard(stream, new SystemClipboard(), coordinates);
    }

    private void copyToClipboard(PrintStream stream, Clipboard clipboard, String coordinates) {
        clipboard.copy(coordinates);
        stream.println("Snippet copied to clipboard.");
        stream.println();
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
