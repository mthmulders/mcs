package it.mulders.mcs.search;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

class CoordinateQueryTest implements WithAssertions {

    @Nested
    @DisplayName("withLimit")
    class WithLimitTest {
        @Test
        void null_limit_should_return_original_object() {
            var query = new CoordinateQuery("foo", "bar");

            var result = query.withLimit(null);

            assertThat(result).isSameAs(query);
        }

        @Test
        void non_null_limit_should_return_new_object() {
            var query = new CoordinateQuery("foo", "bar");

            var result = query.withLimit(1);

            assertThat(result).isNotSameAs(query);
        }

        @Test
        void non_null_limit_should_return_object_with_limit() {
            var query = new CoordinateQuery("foo", "bar");

            var result = query.withLimit(1);

            assertThat(result.searchLimit()).isEqualTo(1);
        }
    }

    @Nested
    @DisplayName("toSolrQuery")
    class ToSolrQueryTest {
        @Test
        void solr_query_should_contain_groupId() {
            var query = new CoordinateQuery("foo", "bar");

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains(URLEncoder.encode("g:foo", StandardCharsets.UTF_8));
        }

        @Test
        void solr_query_should_contain_artifactId() {
            var query = new CoordinateQuery("foo", "bar");

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains(URLEncoder.encode("a:bar", StandardCharsets.UTF_8));
        }

        @Test
        void solr_query_should_contain_version() {
            var query = new CoordinateQuery("foo", "bar", "1.0");

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains(URLEncoder.encode("v:1.0", StandardCharsets.UTF_8));
        }

        @Test
        void solr_query_should_contain_start() {
            var query = new CoordinateQuery("foo", "bar");

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("start=0");
        }

        @Test
        void solr_query_should_contain_limit() {
            var query = new CoordinateQuery("foo", "bar", "1.0")
                    .withLimit(5);

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("rows=5");
        }
    }
}