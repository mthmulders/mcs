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
    private final PomXmlOutput pomXmlOutput = new PomXmlOutput();
    private final TabularSearchOutput tabularSearchOutput = new TabularSearchOutput();

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
            var version = parts.length == 3 ? parts[2] : null;

            searchClient.singularSearch(groupId, artifactId, version)
                    .map(SearchResponse::response)
                    .ifPresent(this::printSingularSearchResponse);
        } else {
            searchClient.wildcardSearch(query)
                    .map(SearchResponse::response)
                    .ifPresent(this::printWildcardSearchResponse);
        }

        return 0;
    }

    private void printSingularSearchResponse(final SearchResponse.Response response) {
        pomXmlOutput.print(response, System.out);
    }

    private void printWildcardSearchResponse(final SearchResponse.Response response) {
        tabularSearchOutput.print(response, System.out);
    }
}
