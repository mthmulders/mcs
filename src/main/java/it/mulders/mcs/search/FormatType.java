package it.mulders.mcs.search;

import it.mulders.mcs.search.printer.*;

import java.util.Arrays;

public enum FormatType {
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

    FormatType(String outputType, CoordinatePrinter printer) {
        this.label = outputType;
        this.printer = printer;
    }

    public CoordinatePrinter getPrinter() {
        return printer;
    }

    public static CoordinatePrinter providePrinter(String text) {
        if (text == null) {
            return Constants.DEFAULT_PRINTER;
        }
        if (text.isBlank()) {
            throw new UnsupportedFormatException("Format type is empty.");
        }

        return Arrays.stream(values())
                .filter(type -> type.label.equals(text))
                .map(FormatType::getPrinter)
                .findFirst()
                .orElseThrow(() -> new UnsupportedFormatException("Format type '%s' is not supported.".formatted(text)));
    }
}
