package it.mulders.mcs.cli;

import it.mulders.mcs.search.SearchCommandHandler;
import picocli.CommandLine;

import java.util.concurrent.Callable;

@CommandLine.Command(
        name = "mcs",
        subcommands = { Cli.SearchCommand.class },
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

    public Cli(final SearchCommandHandler searchCommandHandler) {
        this.searchCommandHandler = searchCommandHandler;
    }

    public SearchCommand createSearchCommand() {
        return new SearchCommand();
    }

    @CommandLine.Command(name = "search", usageHelpAutoWidth = true)
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

        public SearchCommand() {
        }

        @Override
        public Integer call() throws Exception {
            searchCommandHandler.search(this.query);
            return 0;
        }
    }

}
