package it.mulders.mcs.search;

import it.mulders.mcs.common.Result;
import it.mulders.mcs.search.printer.DelegatingOutputPrinter;
import it.mulders.mcs.search.printer.OutputPrinter;

import static it.mulders.mcs.search.Constants.MAX_LIMIT;

public class SearchCommandHandler {
    private final SearchClient searchClient;
    private final DelegatingOutputPrinter outputPrinter;

    public SearchCommandHandler() {
        this(new DelegatingOutputPrinter(), new SearchClient());
    }

    // Visible for testing
    SearchCommandHandler(final DelegatingOutputPrinter outputPrinter, final SearchClient searchClient) {
        this.searchClient = searchClient;
        this.outputPrinter = outputPrinter;
    }

    public void search(final SearchQuery query) {
        performSearch(query)
                .map(response -> performAdditionalSearch(query, response))
                .ifPresent(response -> printResponse(query, response));
    }

    public void setCoordinatePrinter(final OutputPrinter coordinatePrinter) {
        outputPrinter.setCoordinatePrinter(coordinatePrinter);
    }

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

    private SearchResponse.Response combineResponses(SearchResponse.Response response1, SearchResponse.Response response2) {
        var docs = new SearchResponse.Response.Doc[response1.docs().length + response2.docs().length];
        System.arraycopy(response1.docs(), 0, docs, 0, response1.docs().length);
        System.arraycopy(response2.docs(), 0, docs, response1.docs().length, response2.docs().length);
        return new SearchResponse.Response(
                response1.numFound(),
                response2.start(),
                docs
        );
    }

    private Result<SearchResponse.Response> performSearch(final SearchQuery query) {
        return searchClient.search(query)
                .map(SearchResponse::response);
    }

    private void printResponse(final SearchQuery query, final SearchResponse.Response response) {
        outputPrinter.print(query, response, System.out);
    }
}
