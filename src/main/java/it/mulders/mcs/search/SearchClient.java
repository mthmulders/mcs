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

    public Result<SearchResponse> search(final SearchQuery query) {
        var uri = String.format("%s/solrsearch/select?%s", hostname, query.toSolrQuery());

        var request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(uri))
                .build();

        try {
            return client.send(request, new SearchResponseBodyHandler())
                    .body();
        } catch (IOException | InterruptedException e) {
            return new Result.Failure<>(e);
        }
    }
}
