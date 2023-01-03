package it.mulders.mcs.search;

import it.mulders.mcs.search.printer.BuildrOutput;
import it.mulders.mcs.search.printer.CoordinatePrinter;
import it.mulders.mcs.search.printer.GradleGroovyOutput;
import it.mulders.mcs.search.printer.GradleGroovyShortOutput;
import it.mulders.mcs.search.printer.GradleKotlinOutput;
import it.mulders.mcs.search.printer.GrapeOutput;
import it.mulders.mcs.search.printer.IvyXmlOutput;
import it.mulders.mcs.search.printer.LeiningenOutput;
import it.mulders.mcs.search.printer.PomXmlOutput;
import it.mulders.mcs.search.printer.SbtOutput;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FormatTypeTest {

    @ParameterizedTest
    @NullSource
    void return_default_printer_when_format_type_is_null(String parameter) {
        assertThat(FormatType.providePrinter(parameter)).isEqualTo(Constants.DEFAULT_PRINTER);
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "nuget"})
    void throw_exception_when_format_type_is_blank_or_unknown(String parameter) {
        assertThatThrownBy(() -> FormatType.providePrinter(parameter)).isInstanceOf(UnsupportedFormatException.class);
    }

    @Test
    void return_expected_printer_when_format_type_contains_leading_and_trailing_white_spaces() {
        CoordinatePrinter printer = FormatType.providePrinter(" gradle ");
        assertThat(printer.getClass()).isEqualTo(GradleGroovyOutput.class);
        assertThat(printer.getClass()).isEqualTo(GradleGroovyOutput.class);
    }

    @ParameterizedTest
    @MethodSource("formatPrinters")
    <T extends CoordinatePrinter> void return_expected_printer_when_format_type_is_valid(String actual, Class<T> expected) {
        CoordinatePrinter printer = FormatType.providePrinter(actual);
        assertThat(printer.getClass()).isEqualTo(expected);
    }

    private static Stream<Arguments> formatPrinters() {
        return Stream.of(
                Arguments.of("maven", PomXmlOutput.class),
                Arguments.of("gradle", GradleGroovyOutput.class),
                Arguments.of("gradle-short", GradleGroovyShortOutput.class),
                Arguments.of("gradle-kotlin", GradleKotlinOutput.class),
                Arguments.of("sbt", SbtOutput.class),
                Arguments.of("ivy", IvyXmlOutput.class),
                Arguments.of("grape", GrapeOutput.class),
                Arguments.of("leiningen", LeiningenOutput.class),
                Arguments.of("buildr", BuildrOutput.class)
        );
    }
}