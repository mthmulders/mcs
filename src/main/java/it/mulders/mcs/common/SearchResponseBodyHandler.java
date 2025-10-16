package it.mulders.mcs.common;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.jr.ob.JSON;
import com.fasterxml.jackson.jr.ob.JSONObjectException;
import it.mulders.mcs.search.artifact.SearchResponse;
import java.io.IOException;
import java.io.InputStream;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.Map;

public class SearchResponseBodyHandler implements HttpResponse.BodyHandler<Result<SearchResponse>> {
    @Override
    public HttpResponse.BodySubscriber<Result<SearchResponse>> apply(final HttpResponse.ResponseInfo responseInfo) {
        return asObject();
    }

    static HttpResponse.BodySubscriber<Result<SearchResponse>> asObject() {
        var upstream = HttpResponse.BodySubscribers.ofInputStream();

        return HttpResponse.BodySubscribers.mapping(upstream, SearchResponseBodyHandler::toSearchResponse);
    }

    static Result<SearchResponse> toSearchResponse(final InputStream inputStream) {
        try (final InputStream input = inputStream) {
            var map = JSON.std.mapFrom(input);
            return new Result.Success<>(constructSearchResponse(map));
        } catch (final JsonParseException | JSONObjectException joe) {
            return new Result.Failure<>(new IllegalStateException("""
                            Error parsing the search result. This may be a temporary failure from search.maven.org.
                            It can also be caused by requesting a large number of results. If that is the case, try lowering the -l/--limit parameter.
                            If the problem persists, please open a conversation at

                                https://github.com/mthmulders/mcs/discussions

                            Make sure to at least provide your invocation of mcs and the version of mcs you're using.
                            """));
        } catch (final IOException ioe) {
            return new Result.Failure<>(
                    new IllegalStateException("Error processing response: %s%n".formatted(ioe.getLocalizedMessage())));
        }
    }

    static SearchResponse constructSearchResponse(final Map<String, Object> input) {
        return new SearchResponse(null, constructResponse((Map<String, Object>) input.get("response")));
    }

    private static SearchResponse.Response constructResponse(final Map<String, Object> input) {
        return new SearchResponse.Response(
                (int) input.get("numFound"), (int) input.get("start"), constructDocs((List<Map<String, Object>>)
                        input.get("docs")));
    }

    private static SearchResponse.Response.Doc[] constructDocs(List<Map<String, Object>> input) {
        return input.stream().map(SearchResponseBodyHandler::constructDoc).toArray(SearchResponse.Response.Doc[]::new);
    }

    private static SearchResponse.Response.Doc constructDoc(final Map<String, Object> input) {
        return new SearchResponse.Response.Doc(
                (String) input.get("id"),
                (String) input.get("g"),
                (String) input.get("a"),
                (String) input.get("v"),
                (String) input.get("latestVersion"),
                (String) input.get("p"),
                (long) input.get("timestamp"));
    }
}
