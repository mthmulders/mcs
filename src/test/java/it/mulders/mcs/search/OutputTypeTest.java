package it.mulders.mcs.search;

import it.mulders.mcs.search.printer.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;

import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OutputTypeTest {

    @ParameterizedTest
    @NullSource
    void return_default_output_type_when_input_is_null(String parameter) {
        assertThat(OutputType.providePrinter(parameter)).isEqualTo(Constants.DEFAULT_PRINTER);
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "nuget"})
    void throw_exception_when_input_is_blank_or_unknown(String parameter) {
        assertThrows(IllegalArgumentException.class, () -> OutputType.providePrinter(parameter));
    }

    @ParameterizedTest
    @MethodSource("outputPrinters")
    <T extends CoordinatePrinter> void return_expected_output_printer_when_input_is_valid(String actual, Class<T> expected) {
        CoordinatePrinter printer = OutputType.providePrinter(actual);
        assertThat(printer.getClass()).isEqualTo(expected);
    }

    private static Stream<Arguments> outputPrinters() {
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