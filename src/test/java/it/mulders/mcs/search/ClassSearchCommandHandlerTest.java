package it.mulders.mcs.search;

import it.mulders.mcs.common.Result;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;

import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ClassSearchCommandHandlerTest implements WithAssertions {
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
    private final SearchResponse.Response classSearchResponse = new SearchResponse.Response(
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

        @Override
        public Result<SearchResponse> classSearch(String fullClass) {
            return new Result.Success<>(new SearchResponse(null, classSearchResponse));
        }
    };

    private final SearchCommandHandler handler = new SearchCommandHandler(outputPrinter, searchClient);

    @Nested
    @DisplayName("class search")
    class ClassSearch {
        @Test
        void should_invoke_class_search_client() {
            handler.classSearch("org.junit.jupiter.api.DisplayNameGenerator");
            verify(outputPrinter).print(eq(classSearchResponse), any());
        }
    }
}