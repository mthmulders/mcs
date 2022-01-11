package it.mulders.mcs.search;

import java.io.PrintStream;

public class PomXmlOutput implements OutputPrinter {
    public void print(final SearchQuery query, final SearchResponse.Response response, final PrintStream stream) {
        if (response.numFound() != 1) {
            throw new IllegalArgumentException("Search response with more than one result not expected here");
        }

        var doc = response.docs()[0];
        stream.println();
        stream.printf(
                """
                    <dependency>
                        <groupId>%s</groupId>
                        <artifactId>%s</artifactId>
                        <version>%s</version>
                    </dependency>
                """,
                doc.g(),
                doc.a(),
                first(doc.v(), doc.latestVersion())
        );
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
