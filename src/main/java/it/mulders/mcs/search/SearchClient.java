package it.mulders.mcs.search;

import it.mulders.mcs.common.Result;
import it.mulders.mcs.common.SearchResponseBodyHandler;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class SearchClient {
    private final String hostname;
    private final HttpClient client = HttpClient.newHttpClient();

    public SearchClient() {
        this("https://search.maven.org");
    }

    // Visible for testing
    SearchClient(final String hostname) {
        this.hostname = hostname;
    }

    /**
     * Perform a "wildcard" search - that is, a search where the user does not specify
     * if the value they're looking for is a <code>groupId</code> or an <code>artifactId</code>.
     * Fetches at most 20 items.
     *
     * @param query The value to search for.
     * @return Either a {@link SearchResponse} instance or a {@link Throwable}.
     */
    public Result<SearchResponse> wildcardSearch(final String query) {
        var uri = String.format("%s/solrsearch/select?q=%s&start=0&rows=20", hostname, query);
        var request = HttpRequest.newBuilder()
                .uri(URI.create(uri))
                .build();

        try {
            var response = client.send(request, new SearchResponseBodyHandler())
                    .body()
                    .get();

            return new Result.Success<>(response);
        } catch (IOException | InterruptedException e) {
            System.err.printf("Error performing search: %s%n", e.getLocalizedMessage());
            return new Result.Failure<>(e);
        }
    }
}
