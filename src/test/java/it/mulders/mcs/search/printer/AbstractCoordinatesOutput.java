package it.mulders.mcs.search.printer;

import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.SearchResponse;
import org.assertj.core.api.WithAssertions;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

abstract class AbstractCoordinatesOutput implements WithAssertions {

    static final SearchQuery QUERY = SearchQuery.search("org.codehaus.plexus:plexus-utils").build();
    static final SearchResponse.Response RESPONSE = new SearchResponse.Response(1, 0, new SearchResponse.Response.Doc[]{
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
    final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

    void sendQuery(CoordinatesPrinter printer) {
        printer.print(QUERY, RESPONSE, new PrintStream(buffer));
    }
}
