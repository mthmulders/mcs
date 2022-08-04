package it.mulders.mcs.search;

import it.mulders.mcs.common.Result;
import it.mulders.mcs.search.SearchStrategyCalculator.Decision;

public class SearchCommandHandler {
    private final SearchClient searchClient;
    private final OutputPrinter outputPrinter;

    public SearchCommandHandler() {
        this(new DelegatingOutputPrinter(), new SearchClient());
    }

    // Visible for testing
    SearchCommandHandler(
            final OutputPrinter outputPrinter,
            final SearchClient searchClient
    ) {
        this.searchClient = searchClient;
        this.outputPrinter = outputPrinter;
    }

//    public void search(final SearchQuery searchQuery) {
//        search(searchQuery, searchQuery)
//                .ifPresent(response -> printResponse(searchQuery, response));
//    }

    public void search2(final SearchQuery searchQuery) {
        var strategy = new SearchStrategyCalculator(searchClient, searchQuery.searchLimit());
        strategy.select(searchQuery, null).execute(searchQuery)
                .ifPresent(searchResult -> printResponse(searchQuery, searchResult));
    }

    private Result<SearchResult> search2(
            final SearchQuery searchQuery,
            final SearchResponse combinedResponse
    ) {
        var strategy = new SearchStrategyCalculator(searchClient, searchQuery.searchLimit());
        return strategy.select(searchQuery, combinedResponse).execute(searchQuery);



/*
        return searchClient.search(actualSearchQuery)
                .flatMap(response -> {
                    var decision = strategy.select(
                            actualSearchQuery,
                            response
                    );

                    if (decision instanceof Decision.SearchAgain sa) {
                        if (combinedResponse == null) {
                            return search2(originalSearchQuery, sa.newSearchQuery, sa.response);
                        } else {
                            return search2(originalSearchQuery, sa.newSearchQuery, sa.response)
                                    .map(data -> combineResponses(combinedResponse, data))
                                    .flatMap(Result.Success::new);
                        }
                    } else if (decision instanceof Decision.Done d) {
                        return new Result.Success<>(d.response);
                    }

                    return new Result.Failure<>(
                            new IllegalStateException("Did not expect a " + decision.getClass().getName())
                    );
                });
 */
    }

//    private Result<SearchResponse> search(final SearchQuery originalSearchQuery, final SearchQuery actualSearchQuery) {
//        return searchClient.search(actualSearchQuery)
//                .flatMap(response -> {
//                    var decision = searchResponsePostProcessor.process(
//                            originalSearchQuery,
//                            actualSearchQuery,
//                            response
//                    );
//
//                    if (decision instanceof Decision.SearchAgain sa) {
//
//                        searchClient.search(sa.newSearchQuery)
//                                .map(r -> searchResponsePostProcessor.process(originalSearchQuery, actualSearchQuery, response));
//
//                        var combinedResponse = combineResponses(response, sa.response);
//                        return combinedResponse;
//                    } else if (decision instanceof Decision.Done d) {
//                        return new Result.Success<>(d.response);
//                    }
//
//                })
//                .flatMap(decision -> {
//                    if (decision instanceof Decision.SearchAgain sa) {
//                        searchClient.search(actualSearchQuery)
//                                .map(response -> searchResponsePostProcessor.process(originalSearchQuery, actualSearchQuery, response));
//                        return s(originalSearchQuery, sa.newSearchQuery);
//                    } else if (decision instanceof Decision.Done d) {
//                        return new Result.Success<>(d);
//                    }
//
//                    return new Result.Failure<>(null);
//                })
                // By now, it should always be a 'Done'.
//                .map(d -> ((Decision.Done) d).response);
//    }

//    private Result<Decision> s(final SearchQuery originalSearchQuery, final SearchQuery actualSearchQuery) {
//        return searchClient.search(actualSearchQuery)
//                .map(response -> searchResponsePostProcessor.process(originalSearchQuery, actualSearchQuery, response));
//    }

    /*
    private Result<SearchResponse> search2(final SearchQuery originalSearchQuery, final SearchQuery actualSearchQuery) {
        return searchClient.search(actualSearchQuery)
                .map(response -> searchResponsePostProcessor.process(originalSearchQuery, actualSearchQuery, response))
                .map(decision -> {
                    if (decision instanceof Decision.SearchAgain sa) {
                        return search(originalSearchQuery, sa.newSearchQuery);
                    } else if (decision instanceof Decision.Done d) {
                        return new Result.Success<>(d.response);
                    }

//                    var reason = new IllegalStateException(
//                            "Got a decision " + decision.getClass().getName() + " that wasn't covered"
//                    );
//                    return new Result.Failure<SearchResponse>();
                });

//        performSearch(query)
//                .map(response -> performAdditionalSearch(query, response))
//                .ifPresent(response -> printResponse(query, response));
    }
    */

    /*
    private SearchResponse.Response performAdditionalSearch(final SearchQuery query,
                                                            final SearchResponse.Response previousResponse) {
        var lastItemFoundIndex = previousResponse.docs().length;
        var enoughItemsForUserLimit = lastItemFoundIndex >= query.searchLimit();
        var allItemsReceived = lastItemFoundIndex == previousResponse.numFound();
        if (enoughItemsForUserLimit || allItemsReceived) {
            return previousResponse;
        }
        var remainingItems = query.searchLimit() - lastItemFoundIndex;
        var remainingLimit = Math.min(remainingItems, MAX_LIMIT);
        var updatedQuery = query.toBuilder().withStart(lastItemFoundIndex).withLimit(remainingLimit).build();

        return performSearch(updatedQuery)
                .map(response -> combineResponses(previousResponse, response))
                .map(response -> performAdditionalSearch(query, response))
                .value();
    }
    */

        /*
    private SearchResponse combineResponses(SearchResponse response1, SearchResponse response2) {
        var docs1 = response1.response().docs();
        var docs2 = response2.response().docs();

        var docs = new SearchResponse.Response.Doc[docs1.length + docs2.length];

        System.arraycopy(docs1, 0, docs, 0, docs1.length);
        System.arraycopy(docs2, 0, docs, docs1.length, docs2.length);

        return new SearchResponse(
                response1.header(),
                new SearchResponse.Response(
                        response1.response().numFound(),
                        response2.response().start(),
                        docs
                )
        );
    }

    private Result<Decision> performSearch(
            final SearchQuery originalSearchQuery,
            final SearchQuery actualSearchQuery
    ) {
        return searchClient.search(actualSearchQuery)
                .map(response -> searchResponsePostProcessor.process(originalSearchQuery, actualSearchQuery, response));
    }
    */

    private void printResponse(final SearchQuery query, final SearchResult searchResult) {
//        outputPrinter.print(query, searchResult, System.out);
    }
}
