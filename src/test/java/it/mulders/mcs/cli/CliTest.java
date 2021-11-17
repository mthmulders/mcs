package it.mulders.mcs.cli;

import it.mulders.mcs.search.SearchCommandHandler;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CliTest implements WithAssertions {
    private final SearchCommandHandler searchCommandHandler = mock(SearchCommandHandler.class);

    private final Cli cli = new Cli(searchCommandHandler);

    @Nested
    class SearchCommand {
        @Test
        void delegates_to_handler() {
            var program = new CommandLine(cli, new CommandClassFactory(cli));
            program.execute("search", "test");

            verify(searchCommandHandler).search("test");
        }
    }
}