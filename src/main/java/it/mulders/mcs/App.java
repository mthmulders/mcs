package it.mulders.mcs;

import it.mulders.mcs.cli.Cli;
import it.mulders.mcs.cli.CommandClassFactory;
import it.mulders.mcs.search.SearchCommandHandler;
import picocli.CommandLine;

public class App {
    public static void main(final String... args) {
        var cli = new Cli(new SearchCommandHandler());
        var program = new CommandLine(cli, new CommandClassFactory(cli));
        var result = program.execute(args);
        System.exit(result);
    }
}
