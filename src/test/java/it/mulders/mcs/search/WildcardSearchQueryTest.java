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
        void null_limit_should_return_original_object() {
            var query = new WildcardSearchQuery("foo");

            var result = query.withLimit(null);

            assertThat(result).isSameAs(query);
        }

        @Test
        void non_null_limit_should_return_new_object() {
            var query = new WildcardSearchQuery("foo");

            var result = query.withLimit(1);

            assertThat(result).isNotSameAs(query);
        }

        @Test
        void non_null_limit_should_return_object_with_limit() {
            var query = new WildcardSearchQuery("foo");

            var result = query.withLimit(1);

            assertThat(result.searchLimit()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("toSolrQuery")
    class ToSolrQueryTest {
        @Test
        void solr_query_should_contain_limit() {
            var query = new WildcardSearchQuery("foo")
                    .withLimit(5);

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("rows=5");
        }

        @Test
        void solr_query_should_contain_search_term() {
            var query = new WildcardSearchQuery("foo");

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("q=foo");
        }

        @Test
        void solr_query_should_contain_start() {
            var query = new WildcardSearchQuery("foo");

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("start=0");
        }
    }
}