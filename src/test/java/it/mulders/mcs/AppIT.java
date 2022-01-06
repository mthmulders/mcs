package it.mulders.mcs;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class AppIT implements WithAssertions  {
    private final ByteArrayOutputStream output = new ByteArrayOutputStream();
    private PrintStream originalOutput;

    @BeforeEach
    void capture_output() {
        originalOutput = System.out;
        System.setOut(new PrintStream(output));
    }

    @Test
    void should_show_version() {
        App.doMain("-V");
        assertThat(output.toString()).contains("mcs v");
    }

    @Test
    void should_exit_cleanly() {
        assertThat(App.doMain("-V")).isEqualTo(0);
    }

    @AfterEach
    void restore_original_output() {
        System.setOut(originalOutput);
    }
}