package it.mulders.mcs.search;

import it.mulders.mcs.search.printer.BuildrOutput;
import it.mulders.mcs.search.printer.CoordinatePrinter;
import it.mulders.mcs.search.printer.GavOutput;
import it.mulders.mcs.search.printer.GradleGroovyOutput;
import it.mulders.mcs.search.printer.GradleGroovyShortOutput;
import it.mulders.mcs.search.printer.GradleKotlinOutput;
import it.mulders.mcs.search.printer.GrapeOutput;
import it.mulders.mcs.search.printer.IvyXmlOutput;
import it.mulders.mcs.search.printer.JBangOutput;
import it.mulders.mcs.search.printer.LeiningenOutput;
import it.mulders.mcs.search.printer.PomXmlOutput;
import it.mulders.mcs.search.printer.SbtOutput;
import java.util.Arrays;
import java.util.stream.Collectors;

public enum FormatType {
    MAVEN("maven", new PomXmlOutput()),
    GRADLE("gradle", new GradleGroovyOutput()),
    GRADLE_SHORT("gradle-short", new GradleGroovyShortOutput()),
    GRADLE_KOTLIN("gradle-kotlin", new GradleKotlinOutput()),
    SBT("sbt", new SbtOutput()),
    IVY("ivy", new IvyXmlOutput()),
    GRAPE("grape", new GrapeOutput()),
    LEININGEN("leiningen", new LeiningenOutput()),
    BUILDR("buildr", new BuildrOutput()),
    JBANG("jbang", new JBangOutput()),
    GAV("gav", new GavOutput());

    private final String label;
    private final CoordinatePrinter printer;

    FormatType(final String outputType, final CoordinatePrinter printer) {
        this.label = outputType;
        this.printer = printer;
    }

    public CoordinatePrinter getPrinter() {
        return printer;
    }

    static String commaSeparatedLabels() {
        return Arrays.stream(values()).map(type -> type.label).collect(Collectors.joining(", "));
    }

    public static CoordinatePrinter providePrinter(final String text) {
        if (text == null) {
            return Constants.DEFAULT_PRINTER;
        }
        if (text.isBlank()) {
            throw new UnsupportedFormatException(
                    "Empty format type is not allowed. Use on of %s".formatted(commaSeparatedLabels()));
        }

        return Arrays.stream(values())
                .filter(type -> type.label.equals(text.trim()))
                .map(FormatType::getPrinter)
                .findFirst()
                .orElseThrow(() -> new UnsupportedFormatException(
                        "Format type '%s' is not supported. Use one of %s".formatted(text, commaSeparatedLabels())));
    }
}
