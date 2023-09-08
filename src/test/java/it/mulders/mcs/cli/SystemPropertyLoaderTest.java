package it.mulders.mcs.cli;

import java.nio.file.Path;
import java.nio.file.Paths;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class SystemPropertyLoaderTest implements WithAssertions {
    private static final Path SAMPLE = Paths.get("src", "test", "resources", "sample-mcs.config");
    @Test
    void should_load_if_file_exists() {
        var loader = new SystemPropertyLoader(SAMPLE);

        assertThat(loader.getProperties())
                .containsEntry("example.a", "foo");
    }

    @Test
    void should_not_fail_if_file_does_not_exist() {
        var loader = new SystemPropertyLoader(Paths.get("src", "test", "resources", "non-existing-mcs.config"));

        assertThat(loader.getProperties().isEmpty()).isFalse();
    }

    @Test
    void should_delegate_to_System_properties() {
        var loader = new SystemPropertyLoader(SAMPLE);

        // user.dir is not overridden in the sample configuration file
        assertThat(loader.getProperties()).containsKey("user.dir");
    }

    @Test
    void should_override_System_properties() {
        var loader = new SystemPropertyLoader(SAMPLE);

        // user.home is recklessly overridden in the sample configuration file
        assertThat(loader.getProperties()).containsEntry("user.home", "whatever");
    }
}