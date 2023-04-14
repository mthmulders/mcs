package it.mulders.mcs;

import it.mulders.mcs.cli.Cli;
import it.mulders.mcs.cli.CommandClassFactory;
import it.mulders.mcs.common.McsExecutionExceptionHandler;
import picocli.CommandLine;

public class App {
    public static void main(final String... args) {
        System.exit(doMain(args));
    }

    // Visible for testing
    static int doMain(final String... args) {
        var cli = new Cli();
        var program = new CommandLine(cli, new CommandClassFactory(cli))
                .setExecutionExceptionHandler(new McsExecutionExceptionHandler());
        return program.execute(args);
    }
}
