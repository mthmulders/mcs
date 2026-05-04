package it.mulders.mcs.clipboard;

public final class WindowsClipboard implements Clipboard {
    @Override
    public boolean copy(final String text) {
        return Shell.run("clip", text);
    }
}
