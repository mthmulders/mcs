package it.mulders.mcs.search;

import java.util.stream.Stream;

import it.mulders.mcs.common.McsRuntimeException;
import it.mulders.mcs.common.Result;
import it.mulders.mcs.search.printer.DelegatingOutputPrinter;
import it.mulders.mcs.search.printer.OutputPrinter;
import it.mulders.mcs.search.vulnerability.ComponentReportClient;
import it.mulders.mcs.search.vulnerability.ComponentReportResponse.ComponentReport;

import static it.mulders.mcs.search.Constants.MAX_LIMIT;

public class SearchCommandHandler {
    private final SearchClient searchClient;
    private final ComponentReportClient reportClient;
    private final OutputPrinter outputPrinter;
    private final boolean showVulnerabilities;

    public SearchCommandHandler() {
        this(Constants.DEFAULT_PRINTER, false);
    }

    public SearchCommandHandler(final OutputPrinter coordinateOutput, final boolean showVulnerabilities) {
        this(new DelegatingOutputPrinter(coordinateOutput, showVulnerabilities), showVulnerabilities, new SearchClient(), new ComponentReportClient());
    }

    // Visible for testing
    SearchCommandHandler(final OutputPrinter outputPrinter, final boolean showVulnerabilities, final SearchClient searchClient, final ComponentReportClient reportClient) {
        this.searchClient = searchClient;
        this.outputPrinter = outputPrinter;
        this.reportClient = reportClient;
        this.showVulnerabilities = showVulnerabilities;
    }

    public void search(final SearchQuery query) {
        performSearch(query)
                .map(response -> performAdditionalSearch(query, response))
                .ifPresentOrElse(
                        response -> processResponse(query, response),
                        failure -> { throw new McsRuntimeException(failure); }
                );
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

    private void processResponse(final SearchQuery query, final SearchResponse.Response searchResponse) {
        if (showVulnerabilities) {
            reportClient.search(searchResponse.docs())
                .ifPresentOrElse(
                    componentResponse -> processComponentReports(componentResponse.componentReports(), searchResponse.docs()),
                    failure -> { throw new McsRuntimeException(failure); });
        }
        printResponse(query, searchResponse);
    }

    private void processComponentReports(final ComponentReport[] componentReports,
                                         final SearchResponse.Response.Doc[] docs) {
        Stream.of(componentReports)
            .forEach(componentReport ->
                reportClient.assignComponentReport(componentReport, docs));
    }

    private void printResponse(final SearchQuery query, final SearchResponse.Response response) {
        outputPrinter.print(query, response, System.out);
    }
}