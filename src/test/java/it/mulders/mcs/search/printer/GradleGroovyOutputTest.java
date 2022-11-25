package it.mulders.mcs.search.printer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GradleGroovyOutputTest extends AbstractCoordinatesOutput {

    @BeforeEach
    void setUp() {
        sendQuery(new GradleGroovyOutput());
    }

    @Test
    void should_print_gradle_snippet() {
        var xml = buffer.toString();
        assertThat(xml).containsIgnoringWhitespaces(
                "implementation group: 'org.codehaus.plexus', name: 'plexus-utils', version: '3.4.1'");
    }
}