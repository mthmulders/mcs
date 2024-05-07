package it.mulders.mcs;

import it.mulders.mcs.cli.Cli;
import it.mulders.mcs.cli.CommandClassFactory;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class AppTest implements WithAssertions {
  @Nested
  class PrependSearchCommandToArgs {
    @Test
    void should_prepend_search_to_command_line_args() {
      assertThat(App.prependSearchCommandToArgs("info.picocli:picocli"))
          .isEqualTo(new String[] {"search", "info.picocli:picocli"});
      assertThat(App.prependSearchCommandToArgs("-h")).isEqualTo(new String[] {"search", "-h"});
      assertThat(App.prependSearchCommandToArgs()).isEqualTo(new String[] {"search"});
    }
  }

  @Nested
  class IsInvocationWithoutSearchCommand {
    private final Cli cli = new Cli();
    private final CommandLine program = new CommandLine(cli, new CommandClassFactory(cli));

    @Test
    void should_detect_when_search_command_is_not_present() {
      assertThat(App.isInvocationWithoutSearchCommand(program, "info.picocli:picocli")).isTrue();
      assertThat(App.isInvocationWithoutSearchCommand(program, "info.picocli", "picocli")).isTrue();
      assertThat(App.isInvocationWithoutSearchCommand(program, "info.picocli", "picocli", "4.7.5"))
          .isTrue();
    }

    @Test
    void should_detect_when_search_command_is_present() {
      assertThat(App.isInvocationWithoutSearchCommand(program, "search", "info.picocli:picocli"))
          .isFalse();
      assertThat(App.isInvocationWithoutSearchCommand(program, "search", "--help")).isFalse();
    }

    @Test
    void invoking_help_is_not_invoking_search_help() {
      assertThat(App.isInvocationWithoutSearchCommand(program, "--help")).isFalse();
    }

    @Test
    void invoking_help_is_not_invoking_search_version() {
      assertThat(App.isInvocationWithoutSearchCommand(program, "-V")).isFalse();
    }
  }
}
