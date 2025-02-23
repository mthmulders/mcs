package it.mulders.mcs.cli;

import static org.mockito.Mockito.mock;

import it.mulders.mcs.search.SearchCommandHandler;
import org.assertj.core.api.InstanceOfAssertFactories;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CliTest implements WithAssertions {
    private final SearchCommandHandler searchCommandHandler = mock(SearchCommandHandler.class);
    private final SearchCommand searchCommand = new SearchCommand(searchCommandHandler);
    private final ClassSearchCommand classSearchCommand = new ClassSearchCommand(searchCommandHandler);

    private final CommandLine.IFactory commandLineFactory = new CommandLine.IFactory() {
        @Override
        public <K> K create(Class<K> cls) throws Exception {
            if (SearchCommand.class.equals(cls)) {
                return (K) searchCommand;
            } else if (ClassSearchCommand.class.equals(cls)) {
                return (K) classSearchCommand;
            } else {
                return mock(cls);
            }
        }
    };
    private final CommandLine program = new CommandLine(new Cli(), commandLineFactory);

    @Test
    void should_invoke_search_command() {
        // Arrange

        // Act
        program.execute("search", "plexus-utils");

        // Assert
        assertThat(searchCommand)
                .extracting("query", InstanceOfAssertFactories.ARRAY)
                .isEqualTo(new String[] {"plexus-utils"});
        assertThat(searchCommand).extracting("responseFormat").isNull();
        assertThat(classSearchCommand).extracting("limit").isNull();
    }

    @Test
    void should_invoke_search_command_with_limit() {
        // Arrange

        // Act
        program.execute("search", "-l", "3", "plexus-utils");

        // Assert
        assertThat(searchCommand)
                .extracting("limit", InstanceOfAssertFactories.INTEGER)
                .isEqualTo(3);
    }

    @Test
    void should_invoke_search_command_with_format() {
        // Arrange

        // Act
        program.execute("search", "-f", "gradle", "plexus-utils");

        // Assert
        assertThat(searchCommand)
                .extracting("responseFormat", InstanceOfAssertFactories.STRING)
                .isEqualTo("gradle");
    }

    @Test
    void should_invoke_class_search_command() {
        // Arrange

        // Act
        program.execute("class-search", "WithAssertions");

        // Assert
        assertThat(classSearchCommand)
                .extracting("query", InstanceOfAssertFactories.STRING)
                .isEqualTo("WithAssertions");
        assertThat(classSearchCommand)
                .extracting("fullName", InstanceOfAssertFactories.BOOLEAN)
                .isFalse();
        assertThat(classSearchCommand).extracting("limit").isNull();
    }

    @Test
    void should_invoke_class_search_command_with_limit() {
        // Arrange

        // Act
        program.execute("class-search", "-l", "3", "WithAssertions");

        // Assert
        assertThat(classSearchCommand)
                .extracting("query", InstanceOfAssertFactories.STRING)
                .isEqualTo("WithAssertions");
        assertThat(classSearchCommand)
                .extracting("fullName", InstanceOfAssertFactories.BOOLEAN)
                .isFalse();
        assertThat(classSearchCommand)
                .extracting("limit", InstanceOfAssertFactories.INTEGER)
                .isEqualTo(3);
    }

    @Test
    void should_invoke_class_search_command_with_full_classname() {
        // Arrange

        // Act
        program.execute("class-search", "-f", "org.assertj.core.api.WithAssertions");

        // Assert
        assertThat(classSearchCommand)
                .extracting("query", InstanceOfAssertFactories.STRING)
                .isEqualTo("org.assertj.core.api.WithAssertions");
        assertThat(classSearchCommand)
                .extracting("fullName", InstanceOfAssertFactories.BOOLEAN)
                .isTrue();
    }
    //
}
