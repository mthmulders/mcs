package it.mulders.mcs.search;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class ClassnameQueryTest {
    @Nested
    @DisplayName("builder")
    class SearchQueryBuilderTest {

        @Test
        void default_class_search_is_classname_query() {
            SearchQuery query = SearchQuery.classSearch("test").build();
            assertThat(query).isInstanceOf(ClassnameQuery.class);
        }
        @Test
        void can_create_classname_query() {
            SearchQuery query = SearchQuery.classSearch("test").isFullyQualified(false).build();
            assertThat(query).isInstanceOf(ClassnameQuery.class);
        }
    }

    @Nested
    @DisplayName("toSolrQuery")
    class ToSolrQueryTest {
        @Test
        void solr_query_should_contain_limit() {
            var query = SearchQuery.classSearch("foo").withLimit(5).build();

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("rows=5");
        }

        @Test
        void solr_query_should_contain_class_name() {
            var query = SearchQuery.classSearch("foo").build();

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("q=c:foo");
        }

        @Test
        void solr_query_should_contain_class_name_with_package() {
            var query = SearchQuery.classSearch("foo").isFullyQualified(true).build();

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("q=fc:foo");
        }

        @Test
        void solr_query_should_contain_start() {
            var query = SearchQuery.classSearch("foo").build();

            var solrQuery = query.toSolrQuery();

            assertThat(solrQuery).contains("start=0");
        }
    }
}
