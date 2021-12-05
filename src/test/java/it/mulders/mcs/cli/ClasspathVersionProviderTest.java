package it.mulders.mcs.cli;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ClasspathVersionProviderTest implements WithAssertions {
    private CommandLine.IVersionProvider versionProvider = new ClasspathVersionProvider();

    @Test
    void should_read_version_from_classpath() throws Exception {
        // setup is done in src/text/resources/mcs.properties
        assertThat(versionProvider.getVersion()).containsOnly("mcs vUnknown");
    }
}