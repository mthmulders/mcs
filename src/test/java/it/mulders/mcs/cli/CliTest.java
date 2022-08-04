package it.mulders.mcs.cli;

import it.mulders.mcs.search.Constants;
import it.mulders.mcs.search.SearchCommandHandler;
import it.mulders.mcs.search.SearchQuery;
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
    class SearchCommandTest {
        @Test
        void delegates_to_handler() {
            var program = new CommandLine(cli, new CommandClassFactory(cli));
            program.execute("search", "test");

            verify(searchCommandHandler).search2(SearchQuery.search("test").build());
        }

        @Test
        void accepts_space_separated_terms() {
            var program = new CommandLine(cli, new CommandClassFactory(cli));
            program.execute("search", "jakarta", "rs");

            verify(searchCommandHandler).search2(SearchQuery.search("jakarta rs").build());
        }

        @Test
        void accepts_limit_results_parameter() {
            var program = new CommandLine(cli, new CommandClassFactory(cli));
            program.execute("search", "--limit", "3", "test");

            verify(searchCommandHandler).search2(SearchQuery.search("test").withLimit(3).build());
        }
    }

    @Nested
    class ClassSearchCommandTest {
        @Test
        void delegates_to_handler() {
            var program = new CommandLine(cli, new CommandClassFactory(cli));
            program.execute("class-search", "test");

            var query = SearchQuery.classSearch("test")
                    .build();
            verify(searchCommandHandler).search2(query);
        }

        @Test
        void accepts_full_name_parameter() {
            var program = new CommandLine(cli, new CommandClassFactory(cli));
            program.execute("class-search", "--full-name", "test");

            var query = SearchQuery.classSearch("test")
                    .isFullyQualified(true)
                    .withLimit(Constants.DEFAULT_MAX_SEARCH_RESULTS)
                    .build();
            verify(searchCommandHandler).search2(query);
        }
    }
}