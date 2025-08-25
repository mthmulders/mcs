package it.mulders.mcs.dagger;

import it.mulders.mcs.cli.ClassSearchCommand;
import it.mulders.mcs.cli.SearchCommand;
import jakarta.inject.Inject;
import jakarta.inject.Provider;
import picocli.CommandLine;

public class DaggerFactory implements CommandLine.IFactory {
    private final Provider<ClassSearchCommand> classSearchCommandProvider;
    private final Provider<SearchCommand> searchCommandProvider;
    private final CommandLine.IFactory defaultFactory = CommandLine.defaultFactory();

    @Inject
    public DaggerFactory(
            final Provider<ClassSearchCommand> classSearchCommandProvider,
            final Provider<SearchCommand> searchCommandProvider) {
        this.classSearchCommandProvider = classSearchCommandProvider;
        this.searchCommandProvider = searchCommandProvider;
    }

    @Override
    public <K> K create(Class<K> cls) throws Exception {
        return switch (cls.getName()) {
            case "it.mulders.mcs.cli.SearchCommand" -> (K) this.searchCommandProvider.get();
            case "it.mulders.mcs.cli.ClassSearchCommand" -> (K) this.classSearchCommandProvider.get();
            default -> defaultFactory.create(cls);
        };
    }
}
