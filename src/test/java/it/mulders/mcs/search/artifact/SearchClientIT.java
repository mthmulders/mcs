package it.mulders.mcs.search.artifact;

import static com.github.tomakehurst.wiremock.client.WireMock.badRequest;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

import com.github.tomakehurst.wiremock.core.Options;
import com.github.tomakehurst.wiremock.junit5.WireMockExtension;
import it.mulders.mcs.Constants;
import it.mulders.mcs.common.Result;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.http.HttpClient;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SearchClientIT implements WithAssertions {
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @RegisterExtension
    static WireMockExtension wiremock = WireMockExtension.newInstance()
            .options(wireMockConfig()
                    .bindAddress("localhost")
                    .dynamicPort()
                    .useChunkedTransferEncoding(Options.ChunkedEncodingPolicy.NEVER))
            .configureStaticDsl(true)
            .build();

    String getResourceAsString(final String resourceName) {
        try (final InputStream input = getClass().getResourceAsStream(resourceName)) {
            byte[] bytes = input != null ? input.readAllBytes() : new byte[] {};
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (final IOException ioe) {
            return fail("Can't load resource %s", resourceName, ioe);
        }
    }

    @Nested
    @DisplayName("Wildcard search")
    class WildcardSearchTest {
        @Test
        void should_parse_response() {
            // Arrange
            var wmRuntimeInfo = wiremock.getRuntimeInfo();
            stubFor(get(urlPathMatching("/solrsearch/select*"))
                    .willReturn(ok(getResourceAsString("/wildcard-search-response.json"))));

            // Act
            var result = new SearchClient(httpClient, wmRuntimeInfo.getHttpBaseUrl())
                    .search(new WildcardSearchQuery(
                            "plexus-utils", Constants.DEFAULT_MAX_SEARCH_RESULTS, Constants.DEFAULT_START));

            // Assert
            assertThat(result.value()).isNotNull();
            assertThat(result.value().response().numFound()).isEqualTo(2);

            var ids = Arrays.stream(result.value().response().docs())
                    .map(SearchResponse.Response.Doc::id)
                    .toArray(String[]::new);
            assertThat(ids).containsOnly("plexus:plexus-utils", "org.codehaus.plexus:plexus-utils");
        }
    }

    @DisplayName("Singular search")
    @Nested
    class SingularSearchTest {
        @Test
        void should_parse_response_groupId_artifactId() {
            // Arrange
            var wmRuntimeInfo = wiremock.getRuntimeInfo();
            stubFor(get(urlPathMatching("/solrsearch/select*"))
                    .willReturn(ok(getResourceAsString("/group-artifact-search.json"))));

            // Act
            var result = new SearchClient(httpClient, wmRuntimeInfo.getHttpBaseUrl())
                    .search(SearchQuery.search("org.codehaus.plexus:plexus-utils")
                            .build());

            // Assert
            assertThat(result.value()).isNotNull();
            assertThat(result.value().response().numFound()).isEqualTo(1);

            var ids = Arrays.stream(result.value().response().docs())
                    .map(SearchResponse.Response.Doc::id)
                    .toArray(String[]::new);
            assertThat(ids).containsOnly("org.codehaus.plexus:plexus-utils");
        }

        @Test
        void should_parse_response_groupId_artifactId_version() {
            // Arrange
            var wmRuntimeInfo = wiremock.getRuntimeInfo();
            stubFor(get(urlPathMatching("/solrsearch/select*"))
                    .willReturn(ok(getResourceAsString("/group-artifact-version-search.json"))));

            // Act
            var result = new SearchClient(httpClient, wmRuntimeInfo.getHttpBaseUrl())
                    .search(SearchQuery.search("org.codehaus.plexus:plexus-utils:3.4.1")
                            .build());

            // Assert
            assertThat(result.value()).isNotNull();
            assertThat(result.value().response().numFound()).isEqualTo(1);

            var ids = Arrays.stream(result.value().response().docs())
                    .map(SearchResponse.Response.Doc::id)
                    .toArray(String[]::new);
            assertThat(ids).containsOnly("org.codehaus.plexus:plexus-utils:3.4.1");
        }
    }

    @DisplayName("Error handling")
    @Nested
    class ErrorHandlingTest {
        @Test
        void should_gracefully_handle_4xx_response() {
            // Arrange
            var wmRuntimeInfo = wiremock.getRuntimeInfo();
            wiremock.stubFor(get(urlPathMatching("/solrsearch/select*"))
                    .willReturn(badRequest().withBody("Solr returned 400, msg: ")));

            // Act
            var result = new SearchClient(httpClient, wmRuntimeInfo.getHttpBaseUrl())
                    .search(SearchQuery.search("org.codehaus.plexus:plexus-utils")
                            .build());

            // Assert
            assertThat(result).isInstanceOf(Result.Failure.class);
            assertThat(result.cause()).isInstanceOf(IllegalStateException.class);
            assertThat(result.cause()).hasMessageContaining("https://github.com/mthmulders/mcs/discussions");
        }

        @Test
        void should_gracefully_handle_connection_failure() {
            // Very unlikely there's an HTTP server running there...
            var result = new SearchClient(httpClient, "http://localhost:21")
                    .search(new WildcardSearchQuery(
                            "plexus-utils", Constants.DEFAULT_MAX_SEARCH_RESULTS, Constants.DEFAULT_START));

            assertThat(result).isInstanceOf(Result.Failure.class);
            assertThat(result.cause()).isInstanceOf(ConnectException.class);
            assertThat(result.cause().getLocalizedMessage()).contains("localhost:21");
        }
    }
}
