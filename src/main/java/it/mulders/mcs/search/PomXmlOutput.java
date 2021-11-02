package it.mulders.mcs.search;

public class PomXmlOutput {

    private final SearchResponse.Response response;

    public PomXmlOutput(final SearchResponse.Response response) {
        if (response.numFound() != 1) {
            throw new IllegalArgumentException("Search response with more than one result not expected here");
        }
        this.response = response;
    }

    public void print() {
        var doc = response.docs()[0];
        System.out.println();
        System.out.println();
        System.out.printf(
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
        System.out.println();
        System.out.println();
    }
}
