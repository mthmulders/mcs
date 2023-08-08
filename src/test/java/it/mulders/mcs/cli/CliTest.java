package it.mulders.mcs.cli;

import it.mulders.mcs.search.Constants;
import it.mulders.mcs.search.SearchCommandHandler;
import it.mulders.mcs.search.SearchQuery;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import picocli.CommandLine;

import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class CliTest implements WithAssertions {

    private final Cli cli = new Cli();
    private final CommandLine program = new CommandLine(cli, new CommandClassFactory(cli));

    @Nested
    class SearchCommandTest {
        @Test
        void delegates_to_handler() {
            var query = SearchQuery.search("test").build();

            verifySearchExecution(query, "search", "test");
        }

        @Test
        void accepts_space_separated_terms() {
            SearchQuery query = SearchQuery.search("jakarta rs").build();

            verifySearchExecution(query, "search", "jakarta", "rs");
        }

        @Test
        void accepts_limit_results_parameter() {
            var query = SearchQuery.search("test").withLimit(3).build();

            verifySearchExecution(query, "search", "--limit", "3", "test");
        }

        @Test
        void accepts_output_type_parameter() {
            var query = SearchQuery.search("test").build();

            verifySearchExecution(query, "search", "--format", "gradle-short", "test");
        }

        @Test
        void accepts_show_vulnerabilities_parameter() {
            var query = SearchQuery.search("test").build();

            verifySearchExecution(query, "search", "--show-vulnerabilities", "test");
        }
    }

    @Nested
    class ClassSearchCommandTest {

        @Test
        void delegates_to_handler() {
            var query = SearchQuery.classSearch("test").build();

            verifySearchExecution(query, "class-search", "test");
        }

        @Test
        void accepts_full_name_parameter() {
            var query = SearchQuery.classSearch("test")
                    .isFullyQualified(true)
                    .withLimit(Constants.DEFAULT_MAX_SEARCH_RESULTS)
                    .build();

            verifySearchExecution(query, "class-search", "--full-name", "test");
        }
    }

    private void verifySearchExecution(SearchQuery query, String... args) {
        try (MockedConstruction<SearchCommandHandler> mocked = Mockito.mockConstruction(SearchCommandHandler.class)) {
            program.execute(args);
            assertThat(mocked.constructed()).hasSize(1);
            SearchCommandHandler searchCommandHandler = mocked.constructed().get(0);
            verify(searchCommandHandler).search(query);
        }
    }
}