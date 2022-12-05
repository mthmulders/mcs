package it.mulders.mcs.search;

import it.mulders.mcs.common.Result;
import it.mulders.mcs.search.printer.DelegatingOutputPrinter;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SearchCommandHandlerTest implements WithAssertions {
    private final DelegatingOutputPrinter outputPrinter = mock(DelegatingOutputPrinter.class);
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
        public Result<SearchResponse> search(final SearchQuery query) {
            if (query instanceof WildcardSearchQuery) {
                return new Result.Success<>(new SearchResponse(null, wildcardResponse));
            } else if (query instanceof CoordinateQuery cq && cq.version().isBlank()) {
                return new Result.Success<>(new SearchResponse(null, twoPartCoordinateResponse));
            } else {
                return new Result.Success<>(new SearchResponse(null, threePartCoordinateResponse));
            }
        }
    };

    private final SearchCommandHandler handler = new SearchCommandHandler(outputPrinter, searchClient);

    @Nested
    @DisplayName("Wildcard search")
    class WildcardSearchTest {
        @Test
        void should_invoke_search_client() {
            handler.search(SearchQuery.search("plexus-utils").build());
            verify(outputPrinter).print(any(WildcardSearchQuery.class), eq(wildcardResponse), any());
        }
    }

    @Nested
    @DisplayName("Coordinate search")
    class CoordinateSearchTest {
        @Test
        void should_reject_search_terms_in_wrong_format() {
            assertThatThrownBy(() -> handler.search(SearchQuery.search("foo:bar:baz:qux").build()))
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @Test
        void should_invoke_search_client_with_groupId_and_artifactId() {
            handler.search(SearchQuery.search("org.codehaus.plexus:plexus-utils").build());
            verify(outputPrinter).print(any(CoordinateQuery.class), eq(twoPartCoordinateResponse), any());
        }

        @Test
        void should_invoke_search_client_with_groupId_and_artifactId_and_version() {
            handler.search(SearchQuery.search("org.codehaus.plexus:plexus-utils:3.4.1").build());
            verify(outputPrinter).print(any(CoordinateQuery.class), eq(threePartCoordinateResponse), any());
        }
    }
}