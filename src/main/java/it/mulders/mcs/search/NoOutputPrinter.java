package it.mulders.mcs.search;

import java.io.PrintStream;

public class NoOutputPrinter implements OutputPrinter {
    @Override
    public void print(final SearchResponse.Response response, final PrintStream stream) {
        if (response.numFound() != 0) {
            throw new IllegalArgumentException("Search response with any result not expected here");
        }

        stream.println();
        stream.printf("No results found%n");
    }
}
