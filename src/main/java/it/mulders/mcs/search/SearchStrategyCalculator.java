package it.mulders.mcs.search;

import static it.mulders.mcs.search.Constants.MAX_LIMIT;

/**
 * Checks an {@link SearchResponse} and decides what to do next.
 */
public class SearchStrategyCalculator {
    private final SearchClient client;
    private final int requestedLimit;
    sealed interface Decision {
        final class Done implements Decision {
            public final SearchResponse response;

            public Done(final SearchResponse response) {
                this.response = response;
            }
        }

        final class SearchAgain implements Decision {
            public final SearchQuery newSearchQuery;
            public final SearchResponse response;

            public SearchAgain(
                    final SearchQuery originalQuery,
                    final int start,
                    final int limit,
                    final SearchResponse response
            ) {
                this.response = response;
                this.newSearchQuery = originalQuery.toBuilder()
                        .withStart(start)
                        .withLimit(limit)
                        .build();
            }
        }
    }

    public SearchStrategyCalculator(final SearchClient client, final int requestedLimit) {
        this.client = client;
        this.requestedLimit = requestedLimit;
    }

    public SearchStrategy select(final SearchQuery executedQuery, final SearchResponse response) {
        if (response == null) {
            return new DefaultStrategy(client, this);
        }

        var availableItemsCount = response.response().numFound();
        var itemsReturned = response.response().docs().length;
        var allAvailableItemsReturned = itemsReturned == availableItemsCount;
        var lastRemainingItemsReturned = executedQuery.start() + itemsReturned == availableItemsCount;

        if (allAvailableItemsReturned || lastRemainingItemsReturned) {
            return new ReturnStrategy(SearchResult.fromSearchResponse(response));
        }

        var remainingItems = Math.min(requestedLimit, availableItemsCount) - itemsReturned - executedQuery.start();
        var remainingLimit = Math.min(remainingItems, MAX_LIMIT);

        var newSearchQuery = executedQuery.toBuilder()
                .withStart(itemsReturned + executedQuery.start())
                .withLimit(remainingLimit)
                .build();

        return new ModifiedQueryStrategy(client, newSearchQuery, this);
    }
}
