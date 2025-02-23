package it.mulders.mcs.search.printer;

import it.mulders.mcs.search.FormatType;
import jakarta.inject.Inject;

public class OutputFactory {
    @Inject
    public OutputFactory() {}

    public OutputPrinter findOutputPrinter(final String formatName) {
        return FormatType.providePrinter(formatName);
    }
}
