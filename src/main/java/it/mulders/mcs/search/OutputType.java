package it.mulders.mcs.search;

import java.util.Arrays;
import java.util.Optional;

public enum OutputType {
    MAVEN("maven"),
    GRADLE("gradle"),
    GRADLE_SHORT("gradle-short");

    private final String label;

    OutputType(String outputType) {
        this.label = outputType;
    }

    public static OutputType parse(String text) {
        return Optional.ofNullable(text)
                .map(t -> Arrays.stream(values())
                        .filter(type -> type.label.equals(text))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Output format '%s' is not supported.".formatted(text))))
                .orElse(Constants.OUTPUT_TYPE);
    }
}
