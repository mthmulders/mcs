package it.mulders.mcs.search;

public class UnsupportedFormatException extends RuntimeException {

    private final boolean suppressStacktrace;

    public UnsupportedFormatException(final String message) {
        this(message, true);
    }

    public UnsupportedFormatException(final String message, final boolean suppressStacktrace) {
        super(message, null, suppressStacktrace, !suppressStacktrace);
        this.suppressStacktrace = suppressStacktrace;
    }

    @Override
    public String toString() {
        if (suppressStacktrace) {
            return getLocalizedMessage();
        } else {
            return super.toString();
        }
    }
}
