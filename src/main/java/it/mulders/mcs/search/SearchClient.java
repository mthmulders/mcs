package it.mulders.mcs.search;

import it.mulders.mcs.common.Result;
import it.mulders.mcs.common.SearchResponseBodyHandler;

import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.nio.charset.StandardCharsets;

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
        return performSearch(query);
    }

    /**
     * Perform a "specific" search - that is, a search where the user specifies
     * the <code>groupId</code> and <code>artifactId</code>, separated by <code>:</code>.
     * Fetches at most 20 items.
     *
     * @param groupId The groupId to search for.
     * @param artifactId The artifactId to search for.
     * @return Either a {@link SearchResponse} instance or a {@link Throwable}.
     */
    public Result<SearchResponse> singularSearch(final String groupId, final String artifactId) {
        var query = String.format("g:%s AND a:%s", groupId, artifactId);
        return performSearch(query);
    }

    /**
     * Perform a "specific" search - that is, a search where the user specifies
     * the <code>groupId</code>, <code>artifactId</code> and  <code>version</code>, separated by <code>:</code>.
     * Fetches at most 20 items.
     *
     * @param groupId The groupId to search for.
     * @param artifactId The artifactId to search for.
     * @param version The version to search for.
     * @return Either a {@link SearchResponse} instance or a {@link Throwable}.
     */
    public Result<SearchResponse> singularSearch(final String groupId, final String artifactId, final String version) {
        var query = String.format("g:%s AND a:%s AND v:%s", groupId, artifactId, version);
        return performSearch(query);
    }

    /**
     * Perform a "class" search - that is, a search where the user specifies
     * the <code>fullClass</code> name, including the package, and receives
     * the artifacts that contain it.
     * Fetches at most 20 items.
     *
     * @param fullClass the full class name
     * @return Either a {@link SearchResponse} instance or a {@link Throwable}.
     */
    public Result<SearchResponse> classSearch(String fullClass) {
        var query = String.format("fc:%s", fullClass);
        return performSearch(query);
    }

    private Result<SearchResponse> performSearch(final String query) {
        var uri = String.format("%s/solrsearch/select?q=%s&start=0&rows=20", hostname, URLEncoder.encode(query, StandardCharsets.UTF_8));

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
