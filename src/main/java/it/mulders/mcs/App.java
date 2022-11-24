package it.mulders.mcs;

import it.mulders.mcs.cli.Cli;
import it.mulders.mcs.cli.CommandClassFactory;
import picocli.CommandLine;

public class App {
    public static void main(final String... args) {
        System.exit(doMain(args));
    }

    // Visible for testing
    static int doMain(final String... args) {
        var cli = new Cli();
        var program = new CommandLine(cli, new CommandClassFactory(cli));
        return program.execute(args);
    }
}
