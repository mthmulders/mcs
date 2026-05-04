package it.mulders.mcs.clipboard;

public final class MacClipboard implements Clipboard {
    @Override
    public boolean copy(final String text) {
        return Shell.run("pbcopy", text);
    }
}
