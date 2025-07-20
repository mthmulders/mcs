package it.mulders.mcs.search;

import it.mulders.mcs.common.Result;
import it.mulders.mcs.common.SearchResponseBodyHandler;
import jakarta.inject.Inject;
import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

public class SearchClient {
    private final String hostname;
    private final HttpClient client;

    @Inject
    public SearchClient(final HttpClient client) {
        this(client, "https://central.sonatype.com");
    }

    // Visible for testing
    SearchClient(final HttpClient client, final String hostname) {
        this.client = client;
        this.hostname = hostname;
    }

    public Result<SearchResponse> search(final SearchQuery query) {
        var uri = "%s/solrsearch/select?%s".formatted(hostname, query.toSolrQuery());

        var request = HttpRequest.newBuilder()
                .version(HttpClient.Version.HTTP_1_1)
                .uri(URI.create(uri))
                .build();

        try {
            return client.send(request, new SearchResponseBodyHandler()).body();
        } catch (ConnectException e) {
            // The JDK HTTP client throws a ConnectException without a message, we can do better.
            return new Result.Failure<>(new ConnectException("Can't resolve " + hostname));
        } catch (IOException | InterruptedException e) {
            return new Result.Failure<>(e);
        }
    }
}
