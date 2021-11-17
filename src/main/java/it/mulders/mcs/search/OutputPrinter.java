package it.mulders.mcs.search;

import java.io.PrintStream;

public interface OutputPrinter {
    void print(final SearchResponse.Response response, final PrintStream stream);
}
