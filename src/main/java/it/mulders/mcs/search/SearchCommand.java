package it.mulders.mcs.search;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "search", usageHelpAutoWidth = true)
public class SearchCommand implements Callable<Integer> {
    @Parameters(
            arity = "1",
            description = {
                    "What to search for.",
                    "If the search term contains a colon ( : ), it is considered a literal groupId and artifactId",
                    "Otherwise, the search term is considered a wildcard search"
            }
    )
    private String query;

    private final SearchClient searchClient = new SearchClient();
    private final OutputPrinter noOutput = new NoOutputPrinter();
    private final OutputPrinter pomXmlOutput = new PomXmlOutput();
    private final OutputPrinter tabularSearchOutput = new TabularOutputPrinter();

    @Override
    public Integer call() {
        System.out.printf("Searching for %s...%n", query);

        if (query.contains(":")) {
            var parts = query.split(":");
            if (parts.length < 2) {
                System.err.println("Searching a particular artifact requires at least groupId:artifactId");
                System.err.println("and optionally :version");
                System.exit(1);
            }

            var groupId = parts[0];
            var artifactId = parts[1];
            var hasVersion = parts.length == 3;
            if (hasVersion) {
                var version = parts[2];

                searchClient.singularSearch(groupId, artifactId, version)
                        .map(SearchResponse::response)
                        .ifPresent(this::printResponse);
            } else {
                searchClient.singularSearch(groupId, artifactId)
                        .map(SearchResponse::response)
                        .ifPresent(this::printResponse);
            }
        } else {
            searchClient.wildcardSearch(query)
                    .map(SearchResponse::response)
                    .ifPresent(this::printResponse);
        }

        return 0;
    }

    private void printResponse(final SearchResponse.Response response) {
        switch (response.numFound()) {
            case 0 -> noOutput.print(response, System.out);
            case 1 -> pomXmlOutput.print(response, System.out);
            case 2 -> tabularSearchOutput.print(response, System.out);
        }
    }
}
