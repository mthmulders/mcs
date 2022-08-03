package it.mulders.mcs.common;

import it.mulders.mcs.search.SearchResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SearchResponseBodyHandlerTest implements WithAssertions {
    /**
     * This is the response we get when the user uses the "wildcard" search -
     * not looking for an exact coordinate, but looking for something like "plexus-utils".
     */
    @Test
    void parse_wildcard_search_response() {
        // Arrange
        var input = getClass().getResourceAsStream("/wildcard-search-response.json");

        // Act
        var result = SearchResponseBodyHandler.toSearchResponse.apply(input);

        // Assert
        assertThat(result).isInstanceOf(Result.Success.class);
        var response = result.value();

        assertThat(response.header()).isNotNull();
        assertThat(response.header().params()).isNotNull();
        assertThat(response.header().params().q()).isEqualTo("plexus-utils");
        assertThat(response.header().params().start()).isEqualTo(0);
        assertThat(response.header().params().rows()).isEqualTo(20);
        assertThat(response.header().params().sort()).isEqualTo("score desc,timestamp desc,g asc,a asc");

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

    /**
     * This is the response we get when the user searches for a coordinate including a version -
     * e.g. "org.codehaus.plexus:plexus-utils:3.4.1".
     */
    @Test
    void parse_coordinates_with_version_search_response() {
        // Arrange
        var input = getClass().getResourceAsStream("/group-artifact-version-search.json");

        // Act
        var result = SearchResponseBodyHandler.toSearchResponse.apply(input);

        // Assert
        assertThat(result).isInstanceOf(Result.Success.class);
        var response = result.value();

        assertThat(response.header()).isNotNull();
        assertThat(response.header().params()).isNotNull();
        assertThat(response.header().params().q()).isEqualTo("g:org.codehaus.plexus AND a:plexus-utils AND v:3.4.1");
        assertThat(response.header().params().start()).isEqualTo(0);
        assertThat(response.header().params().rows()).isEqualTo(20);
        assertThat(response.header().params().sort()).isEqualTo("score desc,timestamp desc,g asc,a asc,v desc");

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

    /**
     * This is the response we get when the user searches for a coordinate without a version -
     * e.g. "org.codehaus.plexus:plexus-utils".
     */
    @Test
    void parse_coordinates_without_version_search_response() {
        // Arrange
        var input = getClass().getResourceAsStream("/group-artifact-search.json");

        // Act
        var result = SearchResponseBodyHandler.toSearchResponse.apply(input);

        // Assert
        assertThat(result).isInstanceOf(Result.Success.class);
        var response = result.value();

        assertThat(response.header()).isNotNull();
        assertThat(response.header().params()).isNotNull();
        assertThat(response.header().params().q()).isEqualTo("g:org.codehaus.plexus AND a:plexus-utils");
        assertThat(response.header().params().start()).isEqualTo(0);
        assertThat(response.header().params().rows()).isEqualTo(20);
        assertThat(response.header().params().sort()).isEqualTo("score desc,timestamp desc,g asc,a asc");

        assertThat(response.response()).isNotNull();
        assertThat(response.response().numFound()).isEqualTo(1);
        assertThat(response.response().start()).isEqualTo(0);
        assertThat(response.response().docs()).hasSize(1);

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
    }
}