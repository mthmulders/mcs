package it.mulders.mcs.clipboard;

public final class MacClipboard implements Clipboard {
    private final Shell shell;

    public MacClipboard() {
        this(new Shell());
    }

    // for testing
    MacClipboard(final Shell shell) {
        this.shell = shell;
    }

    @Override
    public boolean copy(final String text) {
        return shell.run("pbcopy", text);
    }
}
