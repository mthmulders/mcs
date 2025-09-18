package it.mulders.mcs.search;

import static it.mulders.mcs.Constants.MAX_LIMIT;

import it.mulders.mcs.common.McsRuntimeException;
import it.mulders.mcs.common.Result;
import it.mulders.mcs.search.artifact.SearchClient;
import it.mulders.mcs.search.artifact.SearchQuery;
import it.mulders.mcs.search.artifact.SearchResponse;
import it.mulders.mcs.printer.DelegatingOutputPrinter;
import it.mulders.mcs.printer.OutputFactory;
import it.mulders.mcs.search.vulnerability.ComponentReportClient;
import it.mulders.mcs.search.vulnerability.ComponentReportResponse.ComponentReport;
import jakarta.inject.Inject;
import java.util.stream.Stream;

public class SearchCommandHandler {
    private final OutputFactory outputFactory;
    private final SearchClient searchClient;
    private final ComponentReportClient reportClient;

    @Inject
    public SearchCommandHandler(
            final ComponentReportClient reportClient,
            final OutputFactory outputFactory,
            final SearchClient searchClient) {
        this.outputFactory = outputFactory;
        this.reportClient = reportClient;
        this.searchClient = searchClient;
    }

    public void search(final SearchQuery query, final String outputFormat, final boolean reportVulnerabilities) {
        performSearch(query)
                .map(response -> performAdditionalSearch(query, response))
                .ifPresentOrElse(
                        response -> {
                            if (reportVulnerabilities) {
                                processResponse(query, response);
                            }
                            printResponse(query, response, outputFormat, reportVulnerabilities);
                        },
                        failure -> {
                            throw new McsRuntimeException(failure);
                        });
    }

    private SearchResponse.Response performAdditionalSearch(
            final SearchQuery query, final SearchResponse.Response previousResponse) {
        var lastItemFoundIndex = previousResponse.docs().length;
        var enoughItemsForUserLimit = lastItemFoundIndex >= query.searchLimit();
        var allItemsReceived = lastItemFoundIndex == previousResponse.numFound();
        if (enoughItemsForUserLimit || allItemsReceived) {
            return previousResponse;
        }
        var remainingItems = query.searchLimit() - lastItemFoundIndex;
        var remainingLimit = Math.min(remainingItems, MAX_LIMIT);
        var updatedQuery = query.toBuilder()
                .withStart(lastItemFoundIndex)
                .withLimit(remainingLimit)
                .build();

        return performSearch(updatedQuery)
                .map(response -> combineResponses(previousResponse, response))
                .map(response -> performAdditionalSearch(query, response))
                .value();
    }

    private SearchResponse.Response combineResponses(
            SearchResponse.Response response1, SearchResponse.Response response2) {
        var docs = new SearchResponse.Response.Doc[response1.docs().length + response2.docs().length];
        System.arraycopy(response1.docs(), 0, docs, 0, response1.docs().length);
        System.arraycopy(response2.docs(), 0, docs, response1.docs().length, response2.docs().length);
        return new SearchResponse.Response(response1.numFound(), response2.start(), docs);
    }

    private Result<SearchResponse.Response> performSearch(final SearchQuery query) {
        return searchClient.search(query).map(SearchResponse::response);
    }

    private void processResponse(final SearchQuery query, final SearchResponse.Response searchResponse) {
        reportClient
                .search(searchResponse.docs())
                .ifPresentOrElse(
                        componentResponse ->
                                assignComponentReports(componentResponse.componentReports(), searchResponse.docs()),
                        failure -> {
                            throw new McsRuntimeException(failure);
                        });
    }

    private void assignComponentReports(
            final ComponentReport[] componentReports, final SearchResponse.Response.Doc[] docs) {
        Stream.of(componentReports)
                .forEach(componentReport -> reportClient.assignComponentReport(componentReport, docs));
    }

    private void printResponse(
            final SearchQuery query,
            final SearchResponse.Response response,
            final String outputFormat,
            final boolean showVulnerabilities) {
        var printer = new DelegatingOutputPrinter(outputFactory.findOutputPrinter(outputFormat), showVulnerabilities);
        printer.print(query, response, System.out);
    }
}
