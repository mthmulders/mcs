package it.mulders.mcs.common;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;
import it.mulders.mcs.search.SearchResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class SearchResponseBodyHandler implements HttpResponse.BodyHandler<Result<SearchResponse>> {
    @Override
    public HttpResponse.BodySubscriber<Result<SearchResponse>> apply(final HttpResponse.ResponseInfo responseInfo) {
        return asObject();
    }

    static HttpResponse.BodySubscriber<Result<SearchResponse>> asObject() {
        var upstream = HttpResponse.BodySubscribers.ofInputStream();

        return HttpResponse.BodySubscribers.mapping(upstream, toSearchResponse);
    }

    // Visible for testing
    static final Function<InputStream, Result<SearchResponse>> toSearchResponse = (final InputStream inputStream) -> {
        try (final InputStream input = inputStream) {
            var map = JSON.std.mapFrom(input);
            var response = constructSearchResponse(map);
            return new Result.Success<>(response);
        } catch (final JsonParseException | JSONObjectException joe) {
            return new Result.Failure<>(
                new IllegalStateException(
                        """
                        
                        Error parsing the search result. This may be a temporary failure from search.maven.org.
                        If the problem persists, please open a conversation at
                        
                            https://github.com/mthmulders/mcs/discussions
                        
                        Make sure to at least provide your invocation of mcs and the version of mcs you're using.
                        """
                )
            );
        } catch (final IOException ioe) {
            System.err.printf("Error parsing search response: %s%n", ioe.getLocalizedMessage());
            return new Result.Failure<>(
                    new UncheckedIOException(ioe)
            );
        }
    };

    static SearchResponse constructSearchResponse(final Map<String, Object> input) {
        return new SearchResponse(
                constructHeader((Map<String, Object>) input.get("responseHeader")),
                constructResponse((Map<String, Object>) input.get("response"))
        );
    }

    private static SearchResponse.Header constructHeader(final Map<String, Object> input) {
        return new SearchResponse.Header(
                constructParams((Map<String, Object>) input.get("params"))
        );
    }

    private static SearchResponse.Header.Params constructParams(final Map<String, Object> input) {
        return new SearchResponse.Header.Params(
                (String) input.get("q"),
                Integer.parseInt((String) input.get("start"), 10),
                (String) input.get("sort"),
                Integer.parseInt((String) input.get("rows"), 10)
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
                (String) input.get("v"),
                (String) input.get("latestVersion"),
                (String) input.get("p"),
                (long) input.get("timestamp")
        );
    }
}
