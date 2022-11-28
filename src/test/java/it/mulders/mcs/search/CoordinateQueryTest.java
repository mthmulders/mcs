package it.mulders.mcs.search;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static it.mulders.mcs.search.Constants.*;

class CoordinateQueryTest implements WithAssertions {

    @Nested
    @DisplayName("toSolrQuery")
    class ToSolrQueryTest {
        @ParameterizedTest
        @ValueSource(strings = {"g:foo", "a:bar", "v:1.0"})
        void solr_query_should_contain_basic_parameters(String parameter) {
            var query = createQuery(DEFAULT_MAX_SEARCH_RESULTS, DEFAULT_START);

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains(URLEncoder.encode(parameter, StandardCharsets.UTF_8));
        }

        @ParameterizedTest
        @ValueSource(strings = {"start=3", "rows=5"})
        void solr_query_should_contain_configuring_parameters(String parameter) {
            var query = createQuery(5, 3);

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains(parameter);
        }

        private CoordinateQuery createQuery(Integer maxSearchResults, Integer start) {
            return new CoordinateQuery("foo", "bar", "1.0", maxSearchResults, start);
        }
    }
}