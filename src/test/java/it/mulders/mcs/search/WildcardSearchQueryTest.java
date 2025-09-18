package it.mulders.mcs.search;

import it.mulders.mcs.Constants;
import it.mulders.mcs.search.artifact.WildcardSearchQuery;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

class WildcardSearchQueryTest implements WithAssertions {
    @Nested
    @DisplayName("toSolrQuery")
    class ToSolrQueryTest {
        @Test
        void solr_query_should_contain_limit() {
            var query = new WildcardSearchQuery("foo", 5, Constants.DEFAULT_START);

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("rows=5");
        }

        @Test
        void solr_query_should_contain_search_term() {
            var query = new WildcardSearchQuery("foo", Constants.DEFAULT_MAX_SEARCH_RESULTS, Constants.DEFAULT_START);

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("q=foo");
        }

        @Test
        void solr_query_should_contain_start() {
            var query = new WildcardSearchQuery("foo", Constants.DEFAULT_MAX_SEARCH_RESULTS, 3);

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("start=3");
        }
    }
}
