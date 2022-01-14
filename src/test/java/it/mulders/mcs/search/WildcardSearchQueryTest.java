package it.mulders.mcs.search;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

class WildcardSearchQueryTest implements WithAssertions {
    @Nested
    @DisplayName("withLimit")
    class WithLimitTest {
        @Test
        void simple_query_should_return_wildcard_query() {
            var query = SearchQuery.search("foo").build();

            assertThat(query).isInstanceOf(WildcardSearchQuery.class);
        }

        @Test
        void non_null_limit_should_return_object_with_limit() {
            var query = SearchQuery.search("foo").build();

            assertThat(query.searchLimit()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("toSolrQuery")
    class ToSolrQueryTest {
        @Test
        void solr_query_should_contain_limit() {
            var query = SearchQuery.search("foo").withLimit(5).build();

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("rows=5");
        }

        @Test
        void solr_query_should_contain_search_term() {
            var query = SearchQuery.search("foo").build();

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("q=foo");
        }

        @Test
        void solr_query_should_contain_start() {
            var query = SearchQuery.search("foo").build();

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("start=0");
        }
    }
}