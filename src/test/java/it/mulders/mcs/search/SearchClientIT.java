package it.mulders.mcs.search;

import com.github.tomakehurst.wiremock.junit5.WireMockRuntimeInfo;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.ok;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@WireMockTest
class SearchClientIT implements WithAssertions {
    String getResourceAsString(final String resourceName) {
        try (final InputStream input = getClass().getResourceAsStream(resourceName)) {
            return new String(input.readAllBytes(), StandardCharsets.UTF_8);
        } catch (final IOException ioe) {
            return fail("Can't load resource %s", resourceName, ioe);
        }
    }

    @Nested
    @DisplayName("Wildcard search")
    class WildcardSearch {
        @Test
        void should_parse_response(final WireMockRuntimeInfo wmRuntimeInfo) {
            // Arrange
            stubFor(get(urlPathMatching("/solrsearch/select*"))
                    .willReturn(ok(getResourceAsString("/wildcard-search-response.json"))));

            // Act
            var result = new SearchClient(wmRuntimeInfo.getHttpBaseUrl())
                    .wildcardSearch("plexus-utils");

            // Assert
            assertThat(result.value()).isNotNull();
            assertThat(result.value().response().numFound()).isEqualTo(2);

            var ids = Arrays.stream(result.value().response().docs())
                    .map(SearchResponse.Response.Doc::id)
                    .toArray(String[]::new);
            assertThat(ids).containsOnly("plexus:plexus-utils", "org.codehaus.plexus:plexus-utils");
        }
    }
}