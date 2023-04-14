package it.mulders.mcs.search.printer;

import it.mulders.mcs.search.SearchResponse;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class NoOutputPrinterTest implements WithAssertions {
    private final OutputPrinter printer = new NoOutputPrinter();

    private final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

    @Test
    void should_fail_with_non_empty_result() {
        assertThatThrownBy(() -> printer.print(null, responseWithResult(1), new PrintStream(outputStream)))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> printer.print(null, responseWithResult(2), new PrintStream(outputStream)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void should_print_message_with_empty_result() {
        printer.print(null, responseWithResult(0), new PrintStream(outputStream));
        assertThat(outputStream.toString(StandardCharsets.UTF_8)).contains("No results found");
    }

    private SearchResponse.Response responseWithResult(int count) {
        return new SearchResponse.Response(count, 0, new SearchResponse.Response.Doc[0]);
    }
}