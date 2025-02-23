package it.mulders.mcs.cli;

import it.mulders.mcs.search.SearchCommandHandler;
import it.mulders.mcs.search.SearchQuery;
import jakarta.inject.Inject;
import java.util.concurrent.Callable;
import picocli.CommandLine;

@CommandLine.Command(
        name = "class-search",
        description = "Search artifacts in Maven Central by class name",
        usageHelpAutoWidth = true)
public class ClassSearchCommand implements Callable<Integer> {
    @CommandLine.Parameters(
            arity = "1",
            description = {
                "The class name to search for.",
            })
    private String query;

    @CommandLine.Option(
            names = {"-f", "--full-name"},
            negatable = true,
            arity = "0",
            description = "Class name includes package")
    private boolean fullName;

    @CommandLine.Option(
            names = {"-l", "--limit"},
            description = "Show <count> results",
            paramLabel = "<count>")
    private Integer limit;

    private final SearchCommandHandler searchCommandHandler;

    @Inject
    public ClassSearchCommand(final SearchCommandHandler searchCommandHandler) {
        this.searchCommandHandler = searchCommandHandler;
    }

    // Visible for testing
    ClassSearchCommand(final SearchCommandHandler searchCommandHandler, String query, Integer limit, boolean fullName) {
        this(searchCommandHandler);
        this.fullName = fullName;
        this.limit = limit;
        this.query = query;
    }

    @Override
    public Integer call() {
        System.out.printf("Searching for artifacts containing %s...%n", query);
        var searchQuery = SearchQuery.classSearch(this.query)
                .isFullyQualified(this.fullName)
                .withLimit(limit)
                .build();
        searchCommandHandler.search(searchQuery, "maven", false);
        return 0;
    }
}
