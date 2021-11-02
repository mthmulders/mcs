package it.mulders.mcs.search;

import java.io.PrintStream;

public class PomXmlOutput {
    public void print(final SearchResponse.Response response, final PrintStream stream) {
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
                doc.v()
        );
        stream.println();
    }
}
