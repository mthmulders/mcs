package it.mulders.mcs.dagger;

import dagger.Binds;
import dagger.Module;
import dagger.Provides;
import it.mulders.mcs.cli.Cli;
import it.mulders.mcs.common.McsExecutionExceptionHandler;
import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;

@Module
public interface CommandLineModule {
    //    @Provides static Cli provideCli(SearchCommandHandler searchCommandHandler) {
    //        return new Cli();
    //    }

    @Provides
    static CommandLine provideCommandLine(
            final Cli cli, final DaggerFactory factory, final CommandLine.IExecutionExceptionHandler exceptionHandler) {
        return new CommandLine(cli, factory).setExecutionExceptionHandler(exceptionHandler);
    }

    //    @Binds Cli.SearchCommand bindSearchCommand(final Cli.SearchCommand command);
    //    @Binds Cli.ClassSearchCommand bindClassSearchCommand(final Cli.ClassSearchCommand command);
    //    @Binds SearchCommandHandler bindSearchCommandHandler(final SearchCommandHandler handler);
    @Binds
    IExecutionExceptionHandler bindExecutionExceptionHandler(final McsExecutionExceptionHandler handler);
}
