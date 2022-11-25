package it.mulders.mcs.search.printer;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PomXmlOutputTest extends AbstractCoordinatesOutput {

    @BeforeEach
    void setUp() {
        sendQuery(new PomXmlOutput());
    }

    @Test
    void should_print_pom_snippet() {
        var xml = buffer.toString();
        assertThat(xml).containsIgnoringWhitespaces(
                """
                        <dependency>
                            <groupId>org.codehaus.plexus</groupId>
                            <artifactId>plexus-utils</artifactId>
                            <version>3.4.1</version>
                        </dependency>
                        """);
    }
}