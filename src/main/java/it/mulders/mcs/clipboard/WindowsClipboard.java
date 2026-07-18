package it.mulders.mcs.clipboard;

public final class WindowsClipboard implements Clipboard {
    private final Shell shell;

    public WindowsClipboard() {
        this(new Shell());
    }

    // for testing
    WindowsClipboard(final Shell shell) {
        this.shell = shell;
    }

    @Override
    public boolean copy(final String text) {
        return shell.run("clip", text);
    }
}
