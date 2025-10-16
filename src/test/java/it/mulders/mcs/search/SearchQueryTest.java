package it.mulders.mcs.search;

import it.mulders.mcs.search.artifact.CoordinateQuery;
import it.mulders.mcs.search.artifact.SearchQuery;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SearchQueryTest implements WithAssertions {
    @Nested
    class CoordinateQueryTest {
        @Test
        void should_build_query_with_groupId_and_artifactId() {
            var query = SearchQuery.search("org.codehaus.plexus:plexus-utils").build();
            assertThat(query).isInstanceOf(CoordinateQuery.class).satisfies(q -> {
                assertThat(((CoordinateQuery) q).groupId()).isEqualTo("org.codehaus.plexus");
                assertThat(((CoordinateQuery) q).artifactId()).isEqualTo("plexus-utils");
                assertThat(((CoordinateQuery) q).version()).isNullOrEmpty();
            });
        }

        @Test
        void should_build_query_with_groupId_and_artifactId_and_version() {
            var query =
                    SearchQuery.search("org.codehaus.plexus:plexus-utils:3.4.1").build();
            assertThat(query).isInstanceOf(CoordinateQuery.class).satisfies(q -> {
                assertThat(((CoordinateQuery) q).groupId()).isEqualTo("org.codehaus.plexus");
                assertThat(((CoordinateQuery) q).artifactId()).isEqualTo("plexus-utils");
                assertThat(((CoordinateQuery) q).version()).isEqualTo("3.4.1");
            });
        }

        @Test
        void should_build_query_with_only_groupId() {
            var query = SearchQuery.search("org.codehaus.plexus:").build();
            assertThat(query).isInstanceOf(CoordinateQuery.class).satisfies(q -> {
                assertThat(((CoordinateQuery) q).groupId()).isEqualTo("org.codehaus.plexus");
                assertThat(((CoordinateQuery) q).artifactId()).isNullOrEmpty();
                assertThat(((CoordinateQuery) q).version()).isNullOrEmpty();
            });
        }

        @Test
        void should_build_query_with_only_artifactId() {
            var query = SearchQuery.search(":plexus-utils").build();
            assertThat(query).isInstanceOf(CoordinateQuery.class).satisfies(q -> {
                assertThat(((CoordinateQuery) q).groupId()).isNullOrEmpty();
                assertThat(((CoordinateQuery) q).artifactId()).isEqualTo("plexus-utils");
                assertThat(((CoordinateQuery) q).version()).isNullOrEmpty();
            });
        }

        @Test
        void should_reject_invalid_input() {
            assertThatThrownBy(() -> SearchQuery.search("foo:bar:baz:qux").build())
                    .isInstanceOf(IllegalArgumentException.class);
        }

        @ParameterizedTest
        @CsvSource(textBlock = """
                org.codehaus.plexus:plexus-utils,g:org.codehaus.plexus AND a:plexus-utils
                org.codehaus.plexus:plexus-utils:3.4.1,g:org.codehaus.plexus AND a:plexus-utils AND v:3.4.1
                org.codehaus.plexus:,g:org.codehaus.plexus
                :plexus-utils,a:plexus-utils
                """)
        void should_construct_valid_solr_query(String input, String solrQuery) {
            var result = SearchQuery.search(input).build().toSolrQuery();

            assertThat(result).contains("q=" + URLEncoder.encode(solrQuery, StandardCharsets.UTF_8));
        }
    }
}
