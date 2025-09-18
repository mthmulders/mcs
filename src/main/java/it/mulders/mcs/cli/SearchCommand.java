package it.mulders.mcs.cli;

import it.mulders.mcs.search.SearchCommandHandler;
import it.mulders.mcs.search.artifact.SearchQuery;
import jakarta.inject.Inject;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(
        name = "search",
        description = "Search artifacts in Maven Central by coordinates",
        usageHelpAutoWidth = true)
public class SearchCommand implements Callable<Integer> {
    @CommandLine.Parameters(
            arity = "1..n",
            description = {
                "What to search for.",
                "If the search term contains a colon ( : ), it is considered a literal groupId and artifactId",
                "Otherwise, the search term is considered a wildcard search"
            })
    private String[] query;

    @CommandLine.Option(
            names = {"-l", "--limit"},
            description = "Show <count> results",
            paramLabel = "<count>")
    private Integer limit;

    @CommandLine.Option(
            names = {"-f", "--format"},
            description =
                    """
                    Show result in <type> format
                    Supported types are:
                      maven, gradle, gradle-short, gradle-kotlin, sbt, ivy, grape, leiningen, buildr, jbang, gav
                    """,
            paramLabel = "<type>")
    private String responseFormat;

    @CommandLine.Option(
            names = {"-s", "--show-vulnerabilities"},
            description = "Show reported security vulnerabilities",
            paramLabel = "<vulnerabilities>")
    private boolean showVulnerabilities;

    private final SearchCommandHandler searchCommandHandler;

    @Inject
    public SearchCommand(final SearchCommandHandler searchCommandHandler) {
        this.searchCommandHandler = searchCommandHandler;
    }

    // Visible for testing
    SearchCommand(
            SearchCommandHandler searchCommandHandler,
            String[] query,
            Integer limit,
            String responseFormat,
            boolean showVulnerabilities) {
        this(searchCommandHandler);
        this.limit = limit;
        this.query = query;
        this.responseFormat = responseFormat;
        this.showVulnerabilities = showVulnerabilities;
    }

    @Override
    public Integer call() {
        var combinedQuery = String.join(" ", query);
        System.out.printf("Searching for %s...%n", combinedQuery);
        var searchQuery =
                SearchQuery.search(combinedQuery).withLimit(this.limit).build();

        searchCommandHandler.search(searchQuery, responseFormat, showVulnerabilities);
        return 0;
    }
}
