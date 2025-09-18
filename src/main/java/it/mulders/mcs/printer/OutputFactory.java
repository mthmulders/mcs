package it.mulders.mcs.printer;

import jakarta.inject.Inject;

public class OutputFactory {
    @Inject
    public OutputFactory() {}

    public OutputPrinter findOutputPrinter(final String formatName) {
        return FormatType.providePrinter(formatName);
    }
}
