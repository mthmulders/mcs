package it.mulders.mcs;

import it.mulders.mcs.cli.Cli;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AppIT implements WithAssertions  {
    private final Cli command = new Cli() {
        @Override
        public SearchCommand createSearchCommand() {
            return new SearchCommand() {
                public Integer call() {
                    return 0;
                }
            };
        }

        @Override
        public ClassSearchCommand createClassSearchCommand() {
            return new ClassSearchCommand() {
                public Integer call() {
                    return 0;
                }
            };
        }
    };
    @Test
    void should_show_version() throws Exception {
        var output = tapSystemOut(() -> App.doMain("-V"));
        assertThat(output).contains("mcs v");
    }

    @Test
    void should_exit_cleanly() {
        assertThat(App.doMain("-V")).isEqualTo(0);
    }

    @Test
    void should_exit_nonzero_on_wrong_invocation() {
        assertThat(App.doMain("--does-not-exist")).isNotEqualTo(0);
    }

    @Test
    void runs_without_search_command_specified() {
        assertThat(App.doMain(command, "info.picocli:picocli")).isEqualTo(0);
    }
}