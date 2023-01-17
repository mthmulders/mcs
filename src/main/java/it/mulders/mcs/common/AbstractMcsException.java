package it.mulders.mcs.common;

public abstract class AbstractMcsException extends RuntimeException {

    private final boolean suppressStacktrace;

    protected AbstractMcsException(final String message) {
        this(message, true);
    }

    private AbstractMcsException(final String message, final boolean suppressStacktrace) {
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
