package it.mulders.mcs.search;

import java.io.PrintStream;

/**
 * Output printer that delegates to a different printer, depending on the number of search results.
 */
public class DelegatingOutputPrinter implements OutputPrinter {
    private final OutputPrinter noOutput;
    private final OutputPrinter pomXmlOutput;
    private final OutputPrinter tabularSearchOutput;

    public DelegatingOutputPrinter() {
        this(new NoOutputPrinter(), new PomXmlOutput(), new TabularOutputPrinter());
    }

    // Visible for testing
    DelegatingOutputPrinter(final OutputPrinter noOutput, final OutputPrinter pomXmlOutput, final OutputPrinter tabularSearchOutput) {
        this.noOutput = noOutput;
        this.pomXmlOutput = pomXmlOutput;
        this.tabularSearchOutput = tabularSearchOutput;
    }

    @Override
    public void print(final SearchResponse.Response response, final PrintStream stream) {
        switch (response.numFound()) {
            case 0 -> noOutput.print(response, stream);
            case 1 -> pomXmlOutput.print(response, stream);
            default -> tabularSearchOutput.print(response, stream);
        }
    }
}
