package it.mulders.mcs.cli;

import it.mulders.mcs.search.FormatType;
import it.mulders.mcs.search.SearchCommandHandler;
import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.printer.CoordinatePrinter;
import it.mulders.mcs.search.printer.clipboard.CopyToClipboardConfiguration;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "mcs",
        subcommands = { Cli.SearchCommand.class, Cli.ClassSearchCommand.class },
        usageHelpAutoWidth = true,
        versionProvider = ClasspathVersionProvider.class
)
public class Cli {
    final static String COPY_SHORT_FLAG_NAME = "-c";
    final static String COPY_LONG_FLAG_NAME = "--copy";

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
    private boolean usageHelpRequested;

    public SearchCommand createSearchCommand() {
        return new SearchCommand();
    }

    public ClassSearchCommand createClassSearchCommand() {
        return new ClassSearchCommand();
    }

    private CopyToClipboardConfiguration createCopyToClipboardConfiguration(boolean copyToClipboard) {
        return new CopyToClipboardConfiguration(
                COPY_SHORT_FLAG_NAME,
                COPY_LONG_FLAG_NAME,
                copyToClipboard);
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
                names = { COPY_SHORT_FLAG_NAME, COPY_LONG_FLAG_NAME },
                negatable = true,
                arity = "0",
                description = "Copy dependency definition to the clipboard when a single result is found"
        )
        private boolean copyToClipboard;

        @CommandLine.Option(
                names = { "-l", "--limit" },
                description = "Show <count> results",
                paramLabel = "<count>"
        )
        private Integer limit;

        @CommandLine.Option(
                names = { "-f", "--format" },
                description = """
                        Show result in <type> format
                        Supported types are:
                          maven, gradle, gradle-short, gradle-kotlin, sbt, ivy, grape, leiningen, buildr
                        """,
                paramLabel = "<type>"
        )
        private String responseFormat;

        @Override
        public Integer call() {
            var combinedQuery = String.join(" ", query);
            System.out.printf("Searching for %s...%n", combinedQuery);
            var searchQuery = SearchQuery.search(combinedQuery)
                    .withLimit(this.limit)
                    .build();

            CoordinatePrinter coordinatePrinter = FormatType.providePrinter(responseFormat);
            var searchCommandHandler = new SearchCommandHandler(coordinatePrinter);
            searchCommandHandler.search(searchQuery, createCopyToClipboardConfiguration(copyToClipboard));
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
                names = { COPY_SHORT_FLAG_NAME, COPY_LONG_FLAG_NAME },
                negatable = true,
                arity = "0",
                description = "Copy dependency definition to the clipboard when a single result is found"
        )
        private boolean copyToClipboard;

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
            var searchCommandHandler = new SearchCommandHandler();
            searchCommandHandler.search(searchQuery, createCopyToClipboardConfiguration(copyToClipboard));
            return 0;
        }
    }
}
