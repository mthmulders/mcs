package it.mulders.mcs.search;

import it.mulders.mcs.search.printer.*;

import java.util.Arrays;

public enum OutputType {
    MAVEN("maven", new PomXmlOutput()),
    GRADLE("gradle", new GradleGroovyOutput()),
    GRADLE_SHORT("gradle-short", new GradleGroovyShortOutput());

    private final String label;
    private final OutputPrinter printer;

    OutputType(String outputType, CoordinatesPrinter printer) {
        this.label = outputType;
        this.printer = printer;
    }

    public OutputPrinter getPrinter() {
        return printer;
    }

    public static OutputType parse(String text) {
        if (text == null) {
            return Constants.OUTPUT_TYPE;
        }
        if (text.isBlank()) {
            throw new IllegalArgumentException("Output format is empty.");
        }

        return Arrays.stream(values())
                .filter(type -> type.label.equals(text))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Output format '%s' is not supported.".formatted(text)));
    }
}
