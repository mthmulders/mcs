package it.mulders.mcs.search;

import it.mulders.mcs.search.SearchStrategyCalculator.Decision;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SearchStrategyCalculatorTest implements WithAssertions {
    private final SearchStrategyCalculator calculator = new SearchStrategyCalculator(500);

    @Nested
    @DisplayName("with less items available than asked for")
    class WithLessItemsAvailableThanAskedFor {
        private final SearchQuery query = SearchQuery.search("plexus-utils").withLimit(5).build();

        private final SearchResponse response = new SearchResponse(
                new SearchResponse.Header(
                        new SearchResponse.Header.Params(
                                "plexus-utils", 0, "score desc,timestamp desc,g asc,a asc", 3
                        )
                ),
                new SearchResponse.Response(
                        3,
                        0,
                        new SearchResponse.Response.Doc[3]
                )
        );

        @Test
        void should_return_response_as_is() {
            var result = calculator.select(query, response);

            assertThat(result).isInstanceOf(Decision.Done.class)
                    .extracting(Decision.Done.class::cast)
                    .extracting(d -> d.response)
                    .isEqualTo(response);
        }
    }

    @Nested
    @DisplayName("with a little more items available than returned so far")
    class WithFewMoreItemsAvailableThanReturnedSoFar {
        private final SearchQuery query = SearchQuery.search("plexus-utils").withLimit(15).build();

        private final SearchResponse response = new SearchResponse(
                new SearchResponse.Header(
                        new SearchResponse.Header.Params(
                                "plexus-utils", 0, "score desc,timestamp desc,g asc,a asc", 8
                        )
                ),
                new SearchResponse.Response(
                        15,
                        0,
                        new SearchResponse.Response.Doc[8]
                )
        );

        @Test
        void should_advise_to_query_again() {
            var result = calculator.select(query, response);

            assertThat(result).isInstanceOf(Decision.SearchAgain.class)
                    .extracting(Decision.SearchAgain.class::cast)
                    .extracting(d -> d.newSearchQuery)
                    .isNotNull();;
        }

        @Test
        void should_advise_to_request_as_much_as_possible() {
            var result = calculator.select(query, response);

            assertThat(result)
                    .extracting(Decision.SearchAgain.class::cast)
                    .extracting(d -> d.newSearchQuery.searchLimit())
                    .isEqualTo(7);
        }

        @Test
        void should_advise_to_start_after_found_items() {
            var result = calculator.select(query, response);

            assertThat(result)
                    .extracting(Decision.SearchAgain.class::cast)
                    .extracting(d -> d.newSearchQuery.start())
                    .isEqualTo(8);
        }
    }

    @Nested
    @DisplayName("with a lot more items available than returned so far")
    class WithLotsMoreItemsAvailableThanReturnedSoFar {
        private final SearchQuery originalQuery = SearchQuery.search("plexus-utils").withLimit(350).build();

        private final SearchResponse response1 = new SearchResponse(
                new SearchResponse.Header(
                        new SearchResponse.Header.Params(
                                "plexus-utils", 0, "score desc,timestamp desc,g asc,a asc", 20
                        )
                ),
                new SearchResponse.Response(
                        302,
                        0,
                        new SearchResponse.Response.Doc[20]
                )
        );
        private final SearchResponse response2 = new SearchResponse(
                new SearchResponse.Header(
                        new SearchResponse.Header.Params(
                                "plexus-utils", 20, "score desc,timestamp desc,g asc,a asc", 200
                        )
                ),
                new SearchResponse.Response(
                        302,
                        20,
                        new SearchResponse.Response.Doc[200]
                )
        );
        private final SearchResponse response3 = new SearchResponse(
                new SearchResponse.Header(
                        new SearchResponse.Header.Params(
                                "plexus-utils", 220, "score desc,timestamp desc,g asc,a asc", 82
                        )
                ),
                new SearchResponse.Response(
                        302,
                        220,
                        new SearchResponse.Response.Doc[82]
                )
        );

        @Nested
        @DisplayName("(first try)")
        class FirstTry {

            @Test
            void should_advise_to_query_again() {
                var result = calculator.select(originalQuery, response1);

                assertThat(result).isInstanceOf(Decision.SearchAgain.class)
                        .extracting(Decision.SearchAgain.class::cast)
                        .extracting(d -> d.newSearchQuery)
                        .isNotNull();
                ;
            }

            @Test
            void should_advise_to_request_as_much_as_possible() {
                var result = calculator.select(originalQuery, response1);

                assertThat(result)
                        .extracting(Decision.SearchAgain.class::cast)
                        .extracting(d -> d.newSearchQuery)
                        .extracting(SearchQuery::searchLimit)
                        .isEqualTo(Constants.MAX_LIMIT);
            }

            @Test
            void should_advise_to_start_after_found_items() {
                var result = calculator.select(originalQuery, response1);

                assertThat(result)
                        .extracting(Decision.SearchAgain.class::cast)
                        .extracting(d -> d.newSearchQuery)
                        .extracting(SearchQuery::start)
                        .isEqualTo(20);
            }
        }

        @Nested
        @DisplayName("(second try)")
        class SecondTry {

            @Test
            void should_advise_to_query_again() {
                var query = ((Decision.SearchAgain) calculator.select(originalQuery, response1)).newSearchQuery;
                var result = calculator.select(query, response2);

                assertThat(result).isInstanceOf(Decision.SearchAgain.class)
                        .extracting(Decision.SearchAgain.class::cast)
                        .extracting(d -> d.newSearchQuery)
                        .isNotNull();
                ;
            }

            @Test
            void should_advise_to_request_as_much_as_possible() {
                var query = ((Decision.SearchAgain) calculator.select(originalQuery, response1)).newSearchQuery;
                var result = calculator.select(query, response2);

                assertThat(result)
                        .extracting(Decision.SearchAgain.class::cast)
                        .extracting(d -> d.newSearchQuery)
                        .extracting(SearchQuery::searchLimit)
                        .isEqualTo(82);
            }

            @Test
            void should_advise_to_start_after_found_items() {
                var query = ((Decision.SearchAgain) calculator.select(originalQuery, response1)).newSearchQuery;
                var result = calculator.select(query, response2);

                assertThat(result)
                        .extracting(Decision.SearchAgain.class::cast)
                        .extracting(d -> d.newSearchQuery)
                        .extracting(SearchQuery::start)
                        .isEqualTo(220);
            }
        }

        @Nested
        @DisplayName("(third try)")
        class ThirdTry {

            @Test
            void should_advise_to_stop_querying() {
                var secondQuery = ((Decision.SearchAgain) calculator.select(originalQuery, response1)).newSearchQuery;
                var query = ((Decision.SearchAgain) calculator.select(secondQuery, response2)).newSearchQuery;
                var result = calculator.select(query, response3);

                assertThat(result).isInstanceOf(Decision.Done.class);
            }
        }
    }
}