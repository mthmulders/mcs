package it.mulders.mcs.search.printer.clipboard;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.Test;

class ClipboardTest implements WithAssertions {
    private final Clipboard clipboard = new SystemClipboard();

    @Test
    void paste_result_should_be_equal_to_copied_text() {
        var singleLineText = "'org.codehaus.plexus:plexus-utils:jar:3.4.1'";
        clipboard.copy(singleLineText);
        assertThat(clipboard.paste()).isEqualTo(singleLineText);

        var multiLineText = """
            <dependency>
                <groupId>org.codehaus.plexus</groupId>
                <artifactId>plexus-utils</artifactId>
                <version>3.4.1</version>
            </dependency>
            """;
        clipboard.copy(multiLineText);
        assertThat(clipboard.paste()).isEqualTo(multiLineText);
    }
}