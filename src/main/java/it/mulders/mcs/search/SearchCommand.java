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

    @Override
    public Integer call() {
        System.out.printf("Searching for %s%n", query);
        return 0;
    }
}