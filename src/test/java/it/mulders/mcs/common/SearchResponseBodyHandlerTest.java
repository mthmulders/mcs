package it.mulders.mcs.common;

import it.mulders.mcs.search.SearchResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SearchResponseBodyHandlerTest implements WithAssertions {
    @Nested
    class WildcardSearchResponse {
        @Test
        void parse_response() {
            // Arrange
            var input = getClass().getResourceAsStream("/wildcard-search-response.json");

            // Act
            var response = SearchResponseBodyHandler.toSupplierOfSearchResponse(input).get();

            // Assert
            assertThat(response.response()).isNotNull();
            assertThat(response.response().numFound()).isEqualTo(2);
            assertThat(response.response().start()).isEqualTo(0);
            assertThat(response.response().docs()).hasSize(2);

            assertThat(response.response().docs()).contains(
                    new SearchResponse.Response.Doc(
                            "org.codehaus.plexus:plexus-utils",
                            "org.codehaus.plexus",
                            "plexus-utils",
                            null,
                            "3.4.1",
                            "jar",
                    1630022910000L
                    )
            );

            assertThat(response.response().docs()).contains(
                    new SearchResponse.Response.Doc(
                        "plexus:plexus-utils",
                        "plexus",
                        "plexus-utils",
                        null,
                        "1.0.3",
                        "jar",
                        1131487245000L
                    )
            );
        }
    }

    @Nested
    class SingleSearchResponse {
        @Test
        void parse_response() {
            // Arrange
            var input = getClass().getResourceAsStream("/specific-search-response.json");

            // Act
            var response = SearchResponseBodyHandler.toSupplierOfSearchResponse(input).get();

            // Assert
            assertThat(response.response()).isNotNull();
            assertThat(response.response().numFound()).isEqualTo(1);
            assertThat(response.response().start()).isEqualTo(0);
            assertThat(response.response().docs()).hasSize(1);

            assertThat(response.response().docs()).contains(
                    new SearchResponse.Response.Doc(
                            "org.codehaus.plexus:plexus-utils:3.4.1",
                            "org.codehaus.plexus",
                            "plexus-utils",
                            "3.4.1",
                            null,
                            "jar",
                            1630022910000L
                    )
            );
        }
    }

}