package it.mulders.mcs.dagger;

import dagger.BindsInstance;
import dagger.Component;
import it.mulders.mcs.cli.SearchCommand;
import it.mulders.mcs.cli.SystemPropertyLoader;
import picocli.CommandLine;

@Component(modules = {ClipboardModule.class, CommandLineModule.class, OutputModule.class, SearchModule.class})
public interface Application {
    CommandLine commandLine();

    SystemPropertyLoader systemPropertyLoader();

    SearchCommand searchCommand();

    @Component.Builder
    interface Builder {
        @BindsInstance
        Builder centralHostname(@CentralHostname String userName);

        Application build();
    }
}
