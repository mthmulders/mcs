package it.mulders.mcs.dagger;

import it.mulders.mcs.clipboard.Clipboard;
import it.mulders.mcs.clipboard.LinuxClipboard;
import it.mulders.mcs.clipboard.MacClipboard;
import it.mulders.mcs.clipboard.NoOpClipboard;
import it.mulders.mcs.clipboard.WindowsClipboard;
import java.util.stream.Stream;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junitpioneer.jupiter.ClearSystemProperty;
import org.junitpioneer.jupiter.RestoreSystemProperties;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ClipboardModuleTest implements WithAssertions {

    private static Stream<Arguments> osMapping() {
        return Stream.of(
                Arguments.of("Mac OS X", MacClipboard.class),
                Arguments.of("Windows 10", WindowsClipboard.class),
                Arguments.of("Linux", LinuxClipboard.class),
                Arguments.of("FreeBSD", LinuxClipboard.class),
                Arguments.of("FunOS", NoOpClipboard.class));
    }

    @ParameterizedTest
    @MethodSource("osMapping")
    @RestoreSystemProperties
    void returns_correct_clipboard_command_for_os(String osName, Class<? extends Clipboard> expectedType) {
        System.setProperty("os.name", osName);
        assertThat(ClipboardModule.provideClipboard()).isInstanceOf(expectedType);
    }

    @Test
    @ClearSystemProperty(key = "os.name")
    void returns_noop_clipboard_when_os_name_is_missing() {
        assertThat(ClipboardModule.provideClipboard()).isInstanceOf(NoOpClipboard.class);
    }
}
