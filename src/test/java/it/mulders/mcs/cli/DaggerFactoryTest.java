package it.mulders.mcs.cli;

import it.mulders.mcs.dagger.DaggerFactory;
import jakarta.inject.Provider;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DaggerFactoryTest implements WithAssertions {
    private final SearchCommand searchCommand = new SearchCommand(null);
    private final ClassSearchCommand classSearchCommand = new ClassSearchCommand(null);
    private final Provider<SearchCommand> searchCommandProvider = () -> searchCommand;
    private final Provider<ClassSearchCommand> classSearchCommandProvider = () -> classSearchCommand;
    private final DaggerFactory factory = new DaggerFactory(classSearchCommandProvider, searchCommandProvider);

    @Test
    void can_construct_ClassSearchCommand_instance() throws Exception {
        assertThat(factory.create(ClassSearchCommand.class)).isEqualTo(classSearchCommand);
    }

    @Test
    void can_construct_SearchCommand_instance() throws Exception {
        assertThat(factory.create(SearchCommand.class)).isEqualTo(searchCommand);
    }

    @Test
    void can_construct_arbitrary_other_class() throws Exception {
        assertThat(factory.create(Dummy.class)).isNotNull();
    }

    static class Dummy {}
}
