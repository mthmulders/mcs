package it.mulders.mcs.cli;

import jakarta.inject.Inject;
import picocli.CommandLine;

@CommandLine.Command(
        name = "mcs",
        subcommands = {SearchCommand.class, ClassSearchCommand.class},
        usageHelpAutoWidth = true,
        versionProvider = ClasspathVersionProvider.class)
public class Cli {

    @CommandLine.Option(
            names = {"-V", "--version"},
            description = "Show version number",
            versionHelp = true)
    private boolean showVersion;

    @CommandLine.Option(
            names = {"-h", "--help"},
            description = "Display this help message and exits",
            scope = CommandLine.ScopeType.INHERIT,
            usageHelp = true)
    private boolean usageHelpRequested;
    @Inject
    public Cli() {}
}
