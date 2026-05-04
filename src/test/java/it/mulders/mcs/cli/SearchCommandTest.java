package it.mulders.mcs.cli;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import it.mulders.mcs.search.SearchCommandHandler;
import it.mulders.mcs.search.artifact.SearchQuery;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SearchCommandTest implements WithAssertions {
    private final SearchCommandHandler searchCommandHandler = mock(SearchCommandHandler.class);

    @Test
    void delegates_to_handler() {
        // Arrange
        var command = new SearchCommand(searchCommandHandler, new String[] {"test"}, null, "maven", false, false);

        // Act
        command.call();

        // Assert
        var query = SearchQuery.search("test").build();
        verifyHandlerInvocation("maven", false, false, query);
    }

    @Test
    void accepts_space_separated_terms() {
        // Arrange
        var command =
                new SearchCommand(searchCommandHandler, new String[] {"jakarta", "rs"}, null, "maven", false, false);

        // Act
        command.call();

        // Assert
        var query = SearchQuery.search("jakarta rs").build();
        verifyHandlerInvocation("maven", false, false, query);
    }

    @Test
    void accepts_limit_results_parameter() {
        // Arrange
        var command = new SearchCommand(searchCommandHandler, new String[] {"test"}, 3, "maven", false, false);

        // Act
        command.call();

        // Assert
        var query = SearchQuery.search("test").withLimit(3).build();
        verifyHandlerInvocation("maven", false, false, query);
    }

    @Test
    void accepts_output_type_parameter() {
        // Arrange
        var command =
                new SearchCommand(searchCommandHandler, new String[] {"test"}, null, "gradle-short", false, false);

        // Act
        command.call();

        // Assert
        var query = SearchQuery.search("test").build();
        verifyHandlerInvocation("gradle-short", false, false, query);
    }

    @Test
    void accepts_show_vulnerabilities_parameter() {
        // Arrange
        var command = new SearchCommand(searchCommandHandler, new String[] {"test"}, null, "maven", true, false);

        // Act
        command.call();

        // Assert
        var query = SearchQuery.search("test").build();
        verifyHandlerInvocation("maven", true, false, query);
    }

    @Test
    void accepts_copy_parameter() {
        // Arrange
        var command = new SearchCommand(searchCommandHandler, new String[] {"test"}, null, "maven", false, true);

        // Act
        command.call();

        // Assert
        var query = SearchQuery.search("test").build();
        verifyHandlerInvocation("maven", false, true, query);
    }

    private void verifyHandlerInvocation(
            String outputFormat, boolean reportVulnerabilities, boolean copy, SearchQuery query) {
        var captor = ArgumentCaptor.forClass(SearchQuery.class);
        verify(searchCommandHandler).search(captor.capture(), eq(outputFormat), eq(reportVulnerabilities), eq(copy));
        assertThat(captor.getValue()).isEqualTo(query);
    }
}
