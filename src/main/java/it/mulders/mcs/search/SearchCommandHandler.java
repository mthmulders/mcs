package it.mulders.mcs.search;

public class SearchCommandHandler {
    private final SearchClient searchClient;
    private final OutputPrinter outputPrinter;

    public SearchCommandHandler() {
        this(new DelegatingOutputPrinter(), new SearchClient());
    }

    // Visible for testing
    SearchCommandHandler(final OutputPrinter outputPrinter, final SearchClient searchClient) {
        this.searchClient = searchClient;
        this.outputPrinter = outputPrinter;
    }

    public void search(SearchQuery query) {
        searchClient.search(query)
                .map(SearchResponse::response)
                .ifPresent(response -> printResponse(query, response));
    }

    private void printResponse(final SearchQuery query, final SearchResponse.Response response) {
        outputPrinter.print(query, response, System.out);
    }
}
