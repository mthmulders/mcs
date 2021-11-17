package it.mulders.mcs;

import it.mulders.mcs.search.SearchCommand;
import picocli.CommandLine;

@CommandLine.Command(
        name = "mcs",
        subcommands = { SearchCommand.class },
        usageHelpAutoWidth = true
)
public class App {
    public static void main(final String... args) {
        var program = new CommandLine(new App());
        var result = program.execute(args);
        System.exit(result);
    }
}
