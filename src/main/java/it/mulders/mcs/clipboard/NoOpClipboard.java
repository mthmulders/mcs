package it.mulders.mcs.clipboard;

public final class NoOpClipboard implements Clipboard {
    @Override
    public boolean copy(final String text) {
        return false;
    }
}
