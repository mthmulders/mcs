package it.mulders.mcs.clipboard;

import java.util.List;

public final class LinuxClipboard implements Clipboard {
    private final Shell shell;

    public LinuxClipboard() {
        this(new Shell());
    }

    // for testing
    LinuxClipboard(final Shell shell) {
        this.shell = shell;
    }

    @Override
    public boolean copy(final String text) {
        if (shell.which("wl-copy")) {
            return shell.run("wl-copy", text);
        } else if (shell.which("xclip")) {
            return shell.run(List.of("xclip", "-selection", "clipboard"), text);
        } else if (shell.which("xsel")) {
            return shell.run(List.of("xsel", "--clipboard"), text);
        } else {
            System.err.println("Failed to find supported clipboard command. Tried wl-copy, xclip, and xsel");
            return false;
        }
    }
}
