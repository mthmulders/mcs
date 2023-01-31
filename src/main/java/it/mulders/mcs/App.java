package it.mulders.mcs;

import it.mulders.mcs.cli.Cli;
import it.mulders.mcs.cli.CommandClassFactory;
import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;

public class App {
    private static final IExecutionExceptionHandler executionExceptionHandler = (ex, commandLine, parseResult) -> {
        System.err.println(ex.getLocalizedMessage());
        return -1;
    };

    public static void main(final String... args) {
        System.exit(doMain(args));
    }

    // Visible for testing
    static int doMain(final String... args) {
        var cli = new Cli();
        var program = new CommandLine(cli, new CommandClassFactory(cli))
                .setExecutionExceptionHandler(executionExceptionHandler);
        return program.execute(args);
    }
}
