package it.mulders.mcs.clipboard;

import static it.mulders.mcs.clipboard.Shell.run;
import static it.mulders.mcs.clipboard.Shell.which;

import java.util.List;

public final class LinuxClipboard implements Clipboard {
    @Override
    public boolean copy(final String text) {
        if (which("wl-copy")) {
            return run("wl-copy", text);
        } else if (which("xclip")) {
            return run(List.of("xclip", "-selection", "clipboard"), text);
        } else if (which("xsel")) {
            return run(List.of("xsel", "--clipboard"), text);
        } else {
            System.err.println("Failed to find supported clipboard command. Tried wl-copy, xclip, and xsel");
            return false;
        }
    }
}
