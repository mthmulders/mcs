package it.mulders.mcs.cli;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CommandClassFactoryTest implements WithAssertions {
    private final Cli cli = new Cli();
    private final CommandClassFactory factory = new CommandClassFactory(cli);

    @Test
    void can_construct_search_command_instance() throws Exception {
        assertThat(factory.create(Cli.SearchCommand.class)).isNotNull();
    }

    @Test
    void can_construct_arbitrary_other_class() throws Exception {
        assertThat(factory.create(Dummy.class)).isNotNull();
    }

    static class Dummy {
    }
}