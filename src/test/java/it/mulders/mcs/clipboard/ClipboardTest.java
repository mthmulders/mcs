package it.mulders.mcs.clipboard;

import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ClipboardTest implements WithAssertions {

    private final Shell shell = mock(Shell.class);

    @Test
    void noop_clipboard_returns_false() {
        assertThat(new NoOpClipboard().copy("test")).isFalse();
    }

    @Test
    void mac_clipboard_uses_pbcopy() {
        // Arrange
        when(shell.run("pbcopy", "test")).thenReturn(true);

        // Act
        var result = new MacClipboard(shell).copy("test");

        // Assert
        assertThat(result).isTrue();
        verify(shell).run("pbcopy", "test");
    }

    @Test
    void windows_clipboard_uses_clip() {
        // Arrange
        when(shell.run("clip", "test")).thenReturn(true);

        // Act
        var result = new WindowsClipboard(shell).copy("test");

        // Assert
        assertThat(result).isTrue();
        verify(shell).run("clip", "test");
    }

    @Test
    void linux_clipboard_prefers_wl_copy() {
        // Arrange
        when(shell.which("wl-copy")).thenReturn(true);
        when(shell.run("wl-copy", "test")).thenReturn(true);

        // Act
        var result = new LinuxClipboard(shell).copy("test");

        // Assert
        assertThat(result).isTrue();
        verify(shell).run("wl-copy", "test");
        verify(shell, never()).which("xclip");
    }

    @Test
    void linux_clipboard_falls_back_to_xclip() {
        // Arrange
        when(shell.which("wl-copy")).thenReturn(false);
        when(shell.which("xclip")).thenReturn(true);
        when(shell.run(List.of("xclip", "-selection", "clipboard"), "test")).thenReturn(true);

        // Act
        var result = new LinuxClipboard(shell).copy("test");

        // Assert
        assertThat(result).isTrue();
        verify(shell).run(List.of("xclip", "-selection", "clipboard"), "test");
    }

    @Test
    void linux_clipboard_falls_back_to_xsel() {
        // Arrange
        when(shell.which("wl-copy")).thenReturn(false);
        when(shell.which("xclip")).thenReturn(false);
        when(shell.which("xsel")).thenReturn(true);
        when(shell.run(List.of("xsel", "--clipboard"), "test")).thenReturn(true);

        // Act
        var result = new LinuxClipboard(shell).copy("test");

        // Assert
        assertThat(result).isTrue();
        verify(shell).run(List.of("xsel", "--clipboard"), "test");
    }

    @Test
    void linux_clipboard_returns_false_when_no_supported_tool_is_found() {
        // Arrange
        when(shell.which(anyString())).thenReturn(false);

        // Act
        var result = new LinuxClipboard(shell).copy("test");

        // Assert
        assertThat(result).isFalse();
        verify(shell, never()).run(anyList(), anyString());
    }
}
