package it.mulders.mcs.search.printer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GradleGroovyShortOutputTest extends AbstractCoordinatesOutput {

    @BeforeEach
    void setUp() {
        sendQuery(new GradleGroovyShortOutput());
    }

    @Test
    void should_print_shortened_gradle_snippet() {
        var xml = buffer.toString();
        assertThat(xml).containsIgnoringWhitespaces(
                "'org.codehaus.plexus:plexus-utils:3.4.1'");
    }

}