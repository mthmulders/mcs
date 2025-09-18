package it.mulders.mcs.printer;

import it.mulders.mcs.search.artifact.SearchQuery;
import it.mulders.mcs.search.artifact.SearchResponse;
import java.io.PrintStream;

public interface OutputPrinter {
    void print(final SearchQuery query, final SearchResponse.Response response, final PrintStream stream);
}
