package it.mulders.mcs;

import picocli.CommandLine;

public class App {
    public static void main(final String... args) {
        var program = new CommandLine(
                new Test()
        );
        var result = program.execute(args);
        System.exit(result);
    }
}
