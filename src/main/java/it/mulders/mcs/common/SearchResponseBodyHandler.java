package it.mulders.mcs.common;

import com.fasterxml.jackson.jr.ob.JSON;
import it.mulders.mcs.search.SearchResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class SearchResponseBodyHandler implements HttpResponse.BodyHandler<Supplier<SearchResponse>> {
    @Override
    public HttpResponse.BodySubscriber<Supplier<SearchResponse>> apply(final HttpResponse.ResponseInfo responseInfo) {
        return asObject();
    }

    static HttpResponse.BodySubscriber<Supplier<SearchResponse>> asObject() {
        var upstream = HttpResponse.BodySubscribers.ofInputStream();

        return HttpResponse.BodySubscribers.mapping(
                upstream,
                SearchResponseBodyHandler::toSupplierOfSearchResponse
        );
    }

    static Supplier<SearchResponse> toSupplierOfSearchResponse(final InputStream inputStream) {
        return () -> {
            try (final InputStream input = inputStream) {
                var map = JSON.std.mapFrom(input);
                return constructSearchResponse(map);
            } catch (final IOException ioe) {
                System.err.printf("Error parsing search response: %s%n", ioe.getLocalizedMessage());
                throw new UncheckedIOException(ioe);
            }
        };
    }

    static SearchResponse constructSearchResponse(final Map<String, Object> input) {
        return new SearchResponse(
                null,
                constructResponse((Map<String, Object>) input.get("response"))
        );
    }

    private static SearchResponse.Response constructResponse(final Map<String, Object> input) {
        return new SearchResponse.Response(
                (int) input.get("numFound"),
                (int) input.get("start"),
                constructDocs((List<Map<String, Object>>) input.get("docs"))
        );
    }

    private static SearchResponse.Response.Doc[] constructDocs(List<Map<String, Object>> input) {
        return input.stream()
                .map(SearchResponseBodyHandler::constructDoc)
                .toArray(SearchResponse.Response.Doc[]::new);
    }

    private static SearchResponse.Response.Doc constructDoc(final Map<String, Object> input) {
        return new SearchResponse.Response.Doc(
                (String) input.get("id"),
                (String) input.get("g"),
                (String) input.get("a"),
                (String) input.get("latestVersion"),
                (String) input.get("p"),
                (long) input.get("timestamp")
        );
    }
}
