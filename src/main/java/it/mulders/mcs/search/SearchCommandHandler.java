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

    public void search(final String input, final Integer lastVersions) {
        System.out.printf("Searching for %s...%n", input);

        var query = SearchQuery.fromUserInput(input)
                .withLimit(lastVersions);

        searchClient.search(query)
                .map(SearchResponse::response)
                .ifPresent(this::printResponse);
    }

    private void printResponse(final SearchResponse.Response response) {
        outputPrinter.print(response, System.out);
    }
}
