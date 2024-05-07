package it.mulders.mcs.search.printer;

import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.SearchResponse;
import java.io.PrintStream;

/**
 * Output printer that delegates to a different printer, depending on the number of search results.
 */
public class DelegatingOutputPrinter implements OutputPrinter {
  private final OutputPrinter noOutput;
  private final OutputPrinter coordinateOutput;
  private final OutputPrinter tabularSearchOutput;

  public DelegatingOutputPrinter(final OutputPrinter coordinateOutput) {
    this(coordinateOutput, false);
  }

  public DelegatingOutputPrinter(
      final OutputPrinter coordinateOutput, final boolean showVulnerabilities) {
    this(new NoOutputPrinter(), coordinateOutput, new TabularOutputPrinter(showVulnerabilities));
  }

  // Visible for testing
  DelegatingOutputPrinter(
      final OutputPrinter noOutput,
      final OutputPrinter coordinateOutput,
      final OutputPrinter tabularSearchOutput) {
    this.noOutput = noOutput;
    this.coordinateOutput = coordinateOutput;
    this.tabularSearchOutput = tabularSearchOutput;
  }

  @Override
  public void print(
      final SearchQuery query, final SearchResponse.Response response, final PrintStream stream) {
    switch (response.numFound()) {
      case 0 -> noOutput.print(query, response, stream);
      case 1 -> coordinateOutput.print(query, response, stream);
      default -> tabularSearchOutput.print(query, response, stream);
    }
  }
}
