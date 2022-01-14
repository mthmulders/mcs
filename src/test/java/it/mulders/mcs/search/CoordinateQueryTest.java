package it.mulders.mcs.search;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

class CoordinateQueryTest implements WithAssertions {

    @Nested
    @DisplayName("toSolrQuery")
    class ToSolrQueryTest {
        @Test
        void solr_query_should_contain_groupId() {
            var query = new CoordinateQuery("foo", "bar", null, Constants.DEFAULT_MAX_SEARCH_RESULTS);

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains(URLEncoder.encode("g:foo", StandardCharsets.UTF_8));
        }

        @Test
        void solr_query_should_contain_artifactId() {
            var query = new CoordinateQuery("foo", "bar", null, Constants.DEFAULT_MAX_SEARCH_RESULTS);

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains(URLEncoder.encode("a:bar", StandardCharsets.UTF_8));
        }

        @Test
        void solr_query_should_contain_version() {
            var query = new CoordinateQuery("foo", "bar", "1.0", Constants.DEFAULT_MAX_SEARCH_RESULTS);

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains(URLEncoder.encode("v:1.0", StandardCharsets.UTF_8));
        }

        @Test
        void solr_query_should_contain_start() {
            var query = new CoordinateQuery("foo", "bar", null, Constants.DEFAULT_MAX_SEARCH_RESULTS);

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("start=0");
        }

        @Test
        void solr_query_should_contain_limit() {
            var query = new CoordinateQuery("foo", "bar", "1.0", 5);

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("rows=5");
        }
    }
}