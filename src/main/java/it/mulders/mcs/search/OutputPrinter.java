package it.mulders.mcs.search;

import java.io.PrintStream;

public interface OutputPrinter {
    void print(final SearchQuery query,
               final SearchResponse.Response response,
               final PrintStream stream);
}
