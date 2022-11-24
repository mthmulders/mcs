package it.mulders.mcs.cli;

import it.mulders.mcs.search.OutputType;
import it.mulders.mcs.search.SearchCommandHandler;
import it.mulders.mcs.search.SearchQuery;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "mcs",
        subcommands = { Cli.SearchCommand.class, Cli.ClassSearchCommand.class },
        usageHelpAutoWidth = true,
        versionProvider = ClasspathVersionProvider.class
)
public class Cli {
    private final SearchCommandHandler searchCommandHandler;

    @CommandLine.Option(
            names = { "-V", "--version" },
            description = "Show version number",
            versionHelp = true
    )
    private boolean showVersion;

    @CommandLine.Option(
            names = { "-h", "--help" },
            description = "Display this help message and exits",
            scope = CommandLine.ScopeType.INHERIT,
            usageHelp = true
    )
    boolean usageHelpRequested;

    public Cli(final SearchCommandHandler searchCommandHandler) {
        this.searchCommandHandler = searchCommandHandler;
    }

    public SearchCommand createSearchCommand() {
        return new SearchCommand();
    }

    public ClassSearchCommand createClassSearchCommand() {
        return new ClassSearchCommand();
    }

    @CommandLine.Command(
            name = "search",
            description = "Search artifacts in Maven Central by coordinates",
            usageHelpAutoWidth = true
    )
    public class SearchCommand implements Callable<Integer> {
        @CommandLine.Parameters(
                arity = "1..n",
                description = {
                        "What to search for.",
                        "If the search term contains a colon ( : ), it is considered a literal groupId and artifactId",
                        "Otherwise, the search term is considered a wildcard search"
                }
        )
        private String[] query;

        @CommandLine.Option(
                names = { "-l", "--limit" },
                description = "Show <count> results",
                paramLabel = "<count>"
        )
        private Integer limit;

        @CommandLine.Option(
                names = { "-o", "--output" },
                description = "Show result in <output> format",
                paramLabel = "<output>"
        )
        private OutputType outputType;

        @Override
        public Integer call() {
            var combinedQuery = String.join(" ", query);
            System.out.printf("Searching for %s...%n", combinedQuery);
            var searchQuery = SearchQuery.search(combinedQuery)
                    .withLimit(this.limit)
                    .withOutputType(this.outputType)
                    .build();
            searchCommandHandler.search(searchQuery);
            return 0;
        }
    }

    @CommandLine.Command(
            name = "class-search",
            description = "Search artifacts in Maven Central by class name",
            usageHelpAutoWidth = true
    )
    public class ClassSearchCommand implements Callable<Integer> {
        @CommandLine.Parameters(
                arity = "1",
                description = {
                        "The class name to search for.",
                }
        )
        private String query;

        @CommandLine.Option(
                names = { "-f", "--full-name" },
                negatable = true,
                arity = "0",
                description = "Class name includes package"
        )
        private boolean fullName;

        @CommandLine.Option(
                names = { "-l", "--limit" },
                description = "Show <count> results",
                paramLabel = "<count>"
        )
        private Integer limit;

        @Override
        public Integer call() {
            System.out.printf("Searching for artifacts containing %s...%n", query);
            var searchQuery = SearchQuery.classSearch(this.query)
                    .isFullyQualified(this.fullName)
                    .withLimit(limit)
                    .build();
            searchCommandHandler.search(searchQuery);
            return 0;
        }
    }
}
