package it.mulders.mcs.search;

import it.mulders.mcs.common.Result;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SearchCommandHandlerTest implements WithAssertions {
    private final OutputPrinter outputPrinter = mock(OutputPrinter.class);
    private final SearchResponse.Response wildcardResponse = new SearchResponse.Response(
            0,
            0,
            new SearchResponse.Response.Doc[] {}
    );
    private final SearchResponse.Response twoPartCoordinateResponse = new SearchResponse.Response(
            0,
            0,
            new SearchResponse.Response.Doc[] {}
    );
    private final SearchResponse.Response threePartCoordinateResponse = new SearchResponse.Response(
            0,
            0,
            new SearchResponse.Response.Doc[] {}
    );
    private final SearchClient searchClient = new SearchClient() {
        @Override
        public Result<SearchResponse> wildcardSearch(String query) {
            return new Result.Success<>(new SearchResponse(null, wildcardResponse));
        }

        @Override
        public Result<SearchResponse> singularSearch(String groupId, String artifactId) {
            return new Result.Success<>(new SearchResponse(null, twoPartCoordinateResponse));
        }

        @Override
        public Result<SearchResponse> singularSearch(String groupId, String artifactId, String version) {
            return new Result.Success<>(new SearchResponse(null, threePartCoordinateResponse));
        }
    };

    private final SearchCommandHandler handler = new SearchCommandHandler(outputPrinter, searchClient);

    @Nested
    @DisplayName("Wildcard search")
    class WildcardSearch {
        @Test
        void should_invoke_search_client() {
            handler.search("plexus-utils");
            verify(outputPrinter).print(eq(wildcardResponse), any());
        }
    }

    @Nested
    @DisplayName("Coordinate search")
    class CoordinateSearch {
        @Test
        void should_reject_search_terms_in_wrong_format() {
            assertThatThrownBy(() -> handler.search("foo:bar:baz:qux"))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_invoke_search_client_with_groupId_and_artifactId() {
            handler.search("org.codehaus.plexus:plexus-utils");
            verify(outputPrinter).print(eq(twoPartCoordinateResponse), any());
        }

        @Test
        void should_invoke_search_client_with_groupId_and_artifactId_and_version() {
            handler.search("org.codehaus.plexus:plexus-utils:3.4.1");
            verify(outputPrinter).print(eq(threePartCoordinateResponse), any());
        }
    }
}