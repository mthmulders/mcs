package it.mulders.mcs.search;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import it.mulders.mcs.common.Result;
import it.mulders.mcs.printer.OutputFactory;
import it.mulders.mcs.printer.OutputPrinter;
import it.mulders.mcs.search.artifact.CoordinateQuery;
import it.mulders.mcs.search.artifact.SearchClient;
import it.mulders.mcs.search.artifact.SearchQuery;
import it.mulders.mcs.search.artifact.SearchResponse;
import it.mulders.mcs.search.artifact.WildcardSearchQuery;
import it.mulders.mcs.search.vulnerability.ComponentReportClient;
import javax.net.ssl.SSLHandshakeException;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SearchCommandHandlerTest implements WithAssertions {
    private final ComponentReportClient componentReportClient = mock(ComponentReportClient.class);
    private final OutputPrinter outputPrinter = mock(OutputPrinter.class);
    private final OutputFactory outputFactory = new OutputFactory() {
        @Override
        public OutputPrinter findOutputPrinter(String formatName) {
            return outputPrinter;
        }
    };

    private final SearchResponse.Response wildcardResponse =
            new SearchResponse.Response(0, 0, new SearchResponse.Response.Doc[] {
                new SearchResponse.Response.Doc(
                        "foo:bar", "foo", "bar", "1.0", "1.0", "jar", System.currentTimeMillis())
            });
    private final SearchResponse.Response twoPartCoordinateResponse =
            new SearchResponse.Response(2, 0, new SearchResponse.Response.Doc[] {
                new SearchResponse.Response.Doc(
                        "foo:bar", "foo", "bar", "1.0", "2.0", "jar", System.currentTimeMillis()),
                new SearchResponse.Response.Doc(
                        "foo:bar", "foo", "bar", "2.0", "2.0", "jar", System.currentTimeMillis())
            });
    private final SearchResponse.Response threePartCoordinateResponse =
            new SearchResponse.Response(3, 0, new SearchResponse.Response.Doc[] {
                new SearchResponse.Response.Doc(
                        "foo:bar", "foo", "bar", "1.0", "3.0", "jar", System.currentTimeMillis()),
                new SearchResponse.Response.Doc(
                        "foo:bar", "foo", "bar", "2.0", "3.0", "jar", System.currentTimeMillis()),
                new SearchResponse.Response.Doc(
                        "foo:bar", "foo", "bar", "3.0", "3.0", "jar", System.currentTimeMillis()),
            });
    private final SearchResponse.Response singleArtifactResponse =
            new SearchResponse.Response(1, 0, new SearchResponse.Response.Doc[] {
                new SearchResponse.Response.Doc(
                        "org.codehaus.plexus:plexus-utils:3.4.1",
                        "org.codehaus.plexus",
                        "plexus-utils",
                        "3.4.1",
                        "3.4.1",
                        "jar",
                        System.currentTimeMillis()),
            });
    private final SearchResponse.Response multipleArtifactResponse =
            new SearchResponse.Response(2, 0, new SearchResponse.Response.Doc[] {
                new SearchResponse.Response.Doc(
                        "org.codehaus.plexus:plexus-utils:3.4.0",
                        "org.codehaus.plexus",
                        "plexus-utils",
                        "3.4.0",
                        "3.4.1",
                        "jar",
                        System.currentTimeMillis()),
                new SearchResponse.Response.Doc(
                        "org.codehaus.plexus:plexus-utils:3.4.1",
                        "org.codehaus.plexus",
                        "plexus-utils",
                        "3.4.1",
                        "3.4.1",
                        "jar",
                        System.currentTimeMillis())
            });

    private final SearchClient searchClient = mock(SearchClient.class);
    ;

    private final SearchCommandHandler handler =
            new SearchCommandHandler(componentReportClient, outputFactory, searchClient);

    @Nested
    @DisplayName("Wildcard search")
    class WildcardSearchTest {
        @Test
        void should_invoke_search_client() {
            // Arrange
            when(searchClient.search(any()))
                    .thenReturn(new Result.Success<>(new SearchResponse(singleArtifactResponse)));

            // Act
            handler.search(SearchQuery.search("plexus-utils").build(), "maven", false);

            // Assert
            verify(outputPrinter).print(any(WildcardSearchQuery.class), eq(singleArtifactResponse), any());
        }

        @Test
        void should_propagate_tls_exception_to_runtime_exception() {
            // Arrange
            var result = new Result.Failure<SearchResponse>(
                    new SSLHandshakeException(
                            "PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target"));
            when(searchClient.search(any())).thenReturn(result);

            assertThatThrownBy(
                            () -> handler.search(SearchQuery.search("tls-error").build(), "maven", false))
                    .isInstanceOf(RuntimeException.class);
        }
    }

    @Nested
    @DisplayName("Coordinate search")
    class CoordinateSearchTest {
        @Test
        void should_invoke_search_client_with_groupId_and_artifactId() {
            // Arrange
            when(searchClient.search(any()))
                    .thenReturn(new Result.Success<>(new SearchResponse(singleArtifactResponse)));
            var handler = new SearchCommandHandler(componentReportClient, outputFactory, searchClient);
            var query = SearchQuery.search("org.codehaus.plexus:plexus-utils").build();

            // Act
            handler.search(query, "maven", false);

            // Assert
            verify(outputPrinter).print(eq(query), eq(singleArtifactResponse), any());
        }

        @Test
        void should_invoke_search_client_with_groupId_and_artifactId_and_version() {
            // Arrange
            when(searchClient.search(any()))
                    .thenReturn(new Result.Success<>(new SearchResponse(singleArtifactResponse)));
            var handler = new SearchCommandHandler(componentReportClient, outputFactory, searchClient);

            // Act
            handler.search(
                    SearchQuery.search("org.codehaus.plexus:plexus-utils").build(), "maven", false);

            // Assert
            verify(outputPrinter).print(any(CoordinateQuery.class), eq(singleArtifactResponse), any());
        }

        @Test
        void should_propagate_tls_exception_to_runtime_exception() {
            assertThatThrownBy(() -> handler.search(
                            SearchQuery.search("org.codehaus.plexus:tls-error:3.4.1")
                                    .build(),
                            "maven",
                            false))
                    .isInstanceOf(RuntimeException.class);
        }
    }
}
