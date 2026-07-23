package it.mulders.mcs.cli;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import it.mulders.mcs.Constants;
import it.mulders.mcs.search.SearchCommandHandler;
import it.mulders.mcs.search.artifact.SearchQuery;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ClassSearchCommandTest implements WithAssertions {
    private final SearchCommandHandler searchCommandHandler = mock(SearchCommandHandler.class);

    @Test
    void delegates_to_handler() {
        // Arrange
        var command = new ClassSearchCommand(searchCommandHandler, "test", null, false, false);

        // Act
        command.call();

        // Assert
        var query = SearchQuery.classSearch("test").build();
        verifyHandlerInvocation("maven", false, false, query);
    }

    @Test
    void accepts_full_name_parameter() {
        // Arrange
        var command = new ClassSearchCommand(searchCommandHandler, "test", null, true, false);

        // Act
        command.call();

        // Assert
        var query = SearchQuery.classSearch("test")
                .isFullyQualified(true)
                .withLimit(Constants.DEFAULT_MAX_SEARCH_RESULTS)
                .build();
        verifyHandlerInvocation("maven", false, false, query);
    }

    @Test
    void accepts_copy_parameter() {
        // Arrange
        var command = new ClassSearchCommand(searchCommandHandler, "test", null, false, true);

        // Act
        command.call();

        // Assert
        var query = SearchQuery.classSearch("test").build();
        verifyHandlerInvocation("maven", false, true, query);
    }

    private void verifyHandlerInvocation(
            String outputFormat, boolean reportVulnerabilities, boolean copy, SearchQuery query) {
        var captor = ArgumentCaptor.forClass(SearchQuery.class);
        verify(searchCommandHandler).search(captor.capture(), eq(outputFormat), eq(reportVulnerabilities), eq(copy));
        assertThat(captor.getValue()).isEqualTo(query);
    }
}
