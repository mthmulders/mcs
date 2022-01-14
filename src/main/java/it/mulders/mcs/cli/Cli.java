package it.mulders.mcs.cli;

import it.mulders.mcs.search.SearchCommandHandler;
import it.mulders.mcs.search.SearchQuery;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "mcs",
        subcommands = { Cli.SearchCommand.class, Cli.ClassSearchCommand.class},
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
            description = "Search artifacts in Maven Central",
            usageHelpAutoWidth = true
    )
    public class SearchCommand implements Callable<Integer> {
        @CommandLine.Parameters(
                arity = "1",
                description = {
                        "What to search for.",
                        "If the search term contains a colon ( : ), it is considered a literal groupId and artifactId",
                        "Otherwise, the search term is considered a wildcard search"
                }
        )
        private String query;

        @CommandLine.Option(
                names = { "-l", "--last" },
                description = "Show <count> last versions",
                paramLabel = "<count>"
        )
        private Integer lastVersions;

        public SearchCommand() {
        }

        @Override
        public Integer call() {
            System.out.printf("Searching for %s...%n", query);
            searchCommandHandler.search(SearchQuery.search(this.query).withLimit(this.lastVersions).build());
            return 0;
        }
    }

    @CommandLine.Command(
            name = "class-search",
            description = "Search artifacts in Maven Central",
            usageHelpAutoWidth = true
    )
    public class ClassSearchCommand implements Callable<Integer> {

        @CommandLine.Parameters(
                arity = "1",
                description = {
                        "What to search for.",
                        "This should be a class name. The result is the artifacts that contain the class.",
                }
        )
        private String query;

        @CommandLine.Option(
                names = { "-f", "--full-name" },
                negatable = true,
                arity = "0",
                description = "Show <count> last versions",
                paramLabel = "<count>"
        )
        private Boolean fullName;

        @Override
        public Integer call() {
            System.out.printf("Searching libs that contain %s...%n", query);
            searchCommandHandler.search(SearchQuery.classSearch(this.query).withFullName(this.fullName).build());
            return 0;
        }
    }
}
