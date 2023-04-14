package it.mulders.mcs;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;

class AppIT implements WithAssertions  {
    @Test
    void should_show_version() throws Exception {
        var output = tapSystemOut(() -> App.doMain("-V"));
        assertThat(output).contains("mcs v");
    }

    @Test
    void should_exit_cleanly() {
        assertThat(App.doMain("-V")).isEqualTo(0);
    }

    @Test
    void should_exit_nonzero_on_wrong_invocation() {
        assertThat(App.doMain("--does-not-exist")).isNotEqualTo(0);
    }
}