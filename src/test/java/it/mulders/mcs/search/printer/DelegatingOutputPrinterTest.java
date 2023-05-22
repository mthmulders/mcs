package it.mulders.mcs.search.printer;

import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.SearchResponse;
import it.mulders.mcs.search.printer.clipboard.CopyToClipboardConfig;
import org.apache.commons.io.output.NullOutputStream;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.io.PrintStream;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class DelegatingOutputPrinterTest implements WithAssertions {
    private final OutputPrinter noOutput = mock(OutputPrinter.class);
    private final OutputPrinter coordinateOutput = mock(OutputPrinter.class);
    private final OutputPrinter tabularSearchOutput = mock(OutputPrinter.class);

    private final DelegatingOutputPrinter printer = new DelegatingOutputPrinter(noOutput, coordinateOutput, tabularSearchOutput);

    private final SearchQuery query = SearchQuery.search("org.codehaus.plexus:plexus-utils").build();
    private final PrintStream outputStream = new PrintStream(NullOutputStream.nullOutputStream());

    @Test
    void no_results_delegate() {
        printer.print(query, responseWithResult(0), outputStream, null);
        verify(noOutput).print(eq(query), any(), eq(outputStream), any());
        verify(coordinateOutput, never()).print(any(), any(), any(), any());
        verify(tabularSearchOutput, never()).print(any(), any(), any(), any());
    }

    @Test
    void single_result_delegate() {
        var configuration = new CopyToClipboardConfig("-dopt",
                "--delegating-output-printer-test", true);

        printer.print(query, responseWithResult(1), outputStream, configuration);
        verify(noOutput, never()).print(any(), any(), any(), any());
        verify(coordinateOutput).print(eq(query), any(), eq(outputStream), eq(configuration));
        verify(tabularSearchOutput, never()).print(any(), any(), any(), any());
    }

    @Test
    void multiple_results_delegate() {
        printer.print(query, responseWithResult(2), outputStream, null);
        verify(noOutput, never()).print(any(), any(), any(), any());
        verify(coordinateOutput, never()).print(any(), any(), any(), any());
        verify(tabularSearchOutput).print(eq(query), any(), eq(outputStream), any());
    }

    private SearchResponse.Response responseWithResult(int count) {
        return new SearchResponse.Response(count, 0, new SearchResponse.Response.Doc[0]);
    }
}