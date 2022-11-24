package it.mulders.mcs.search.printer;

import it.mulders.mcs.search.Constants;
import it.mulders.mcs.search.OutputType;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class OutputTypeTest {

    @ParameterizedTest
    @NullSource
    void return_default_output_type_when_input_is_null(String parameter) {
        assertThat(OutputType.parse(parameter)).isEqualTo(Constants.OUTPUT_TYPE);
    }

    @ParameterizedTest
    @EmptySource
    @ValueSource(strings = {" ", "nuget"})
    void throw_exception_when_input_is_blank_or_unknown(String parameter) {
        assertThrows(IllegalArgumentException.class, () -> OutputType.parse(parameter));
    }

    @ParameterizedTest
    @CsvSource(value = {
            "maven,MAVEN",
            "gradle,GRADLE",
            "gradle-short,GRADLE_SHORT",
    })
    void return_expected_output_type_when_input_is_valid(String actual, OutputType expected) {
        OutputType type = OutputType.parse(actual);
        assertThat(type).isEqualTo(expected);
    }
}