package it.mulders.mcs.search.printer;

import it.mulders.mcs.common.AbstractMcsException;

public class UnexpectedResultException extends AbstractMcsException {

    public UnexpectedResultException(final String message) {
        super(message);
    }
}
