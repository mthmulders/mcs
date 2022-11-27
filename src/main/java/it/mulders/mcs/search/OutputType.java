package it.mulders.mcs.search;

import it.mulders.mcs.search.printer.*;

import java.util.Arrays;

public enum OutputType {
    MAVEN("maven", new PomXmlOutput()),
    GRADLE("gradle", new GradleGroovyOutput()),
    GRADLE_SHORT("gradle-short", new GradleGroovyShortOutput()),
    GRADLE_KOTLIN("gradle-kotlin", new GradleKotlinOutput()),
    SBT("sbt", new SbtOutput()),
    IVY("ivy", new IvyXmlOutput()),
    GRAPE("grape", new GrapeOutput()),
    LEININGEN("leiningen", new LeiningenOutput()),
    BUILDR("buildr", new BuildrOutput());

    private final String label;
    private final CoordinatePrinter printer;

    OutputType(String outputType, CoordinatePrinter printer) {
        this.label = outputType;
        this.printer = printer;
    }

    private CoordinatePrinter getPrinter() {
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

    public static CoordinatePrinter providePrinter(String text) {
        if (text == null) {
            return Constants.DEFAULT_PRINTER;
        }
        if (text.isBlank()) {
            throw new IllegalArgumentException("Output format is empty.");
        }

        return Arrays.stream(values())
                .filter(type -> type.label.equals(text))
                .map(OutputType::getPrinter)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Output format '%s' is not supported.".formatted(text)));
    }
}
