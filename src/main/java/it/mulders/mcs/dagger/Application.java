package it.mulders.mcs.dagger;

import dagger.Component;
import it.mulders.mcs.cli.SearchCommand;
import it.mulders.mcs.cli.SystemPropertyLoader;
import picocli.CommandLine;

@Component(modules = {CommandLineModule.class, OutputModule.class, SearchModule.class})
public interface Application {
    CommandLine commandLine();

    SystemPropertyLoader systemPropertyLoader();

    SearchCommand searchCommand();
}
