package it.mulders.mcs.search;

public class SearchCommandHandler {
    private final SearchClient searchClient = new SearchClient();
    private final DelegatingOutputPrinter outputPrinter = new DelegatingOutputPrinter();

    public void search(final String query) {
        System.out.printf("Searching for %s...%n", query);

        if (isCoordinateSearch(query)) {
            performCoordinateSearch(query);
        } else {
            performWildcardSearch(query);
        }
    }

    private void performWildcardSearch(final String query) {
        searchClient.wildcardSearch(query)
                .map(SearchResponse::response)
                .ifPresent(this::printResponse);
    }

    private void performCoordinateSearch(final String query) {
        var parts = query.split(":");
        if (parts.length < 2) {
            var msg = """
                        Searching a particular artifact requires at least groupId:artifactId and optionally :version
                        """;
            throw new IllegalArgumentException(msg);
        }

        var groupId = parts[0];
        var artifactId = parts[1];
        var hasVersion = parts.length == 3;
        if (hasVersion) {
            var version = parts[2];

            searchClient.singularSearch(groupId, artifactId, version)
                    .map(SearchResponse::response)
                    .ifPresent(this::printResponse);
        } else {
            searchClient.singularSearch(groupId, artifactId)
                    .map(SearchResponse::response)
                    .ifPresent(this::printResponse);
        }
    }

    private void printResponse(final SearchResponse.Response response) {
        outputPrinter.print(response, System.out);
    }

    private boolean isCoordinateSearch(final String query) {
        return query.contains(":");
    }
}
