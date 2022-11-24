package it.mulders.mcs.search;

import java.util.Arrays;

public enum OutputType {
    MAVEN("maven"),
    GRADLE("gradle"),
    GRADLE_SHORT("gradle-short");

    private final String label;

    OutputType(String outputType) {
        this.label = outputType;
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
