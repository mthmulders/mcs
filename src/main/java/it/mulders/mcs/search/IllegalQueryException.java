package it.mulders.mcs.search;

import it.mulders.mcs.common.AbstractMcsException;

public class IllegalQueryException extends AbstractMcsException {

    public IllegalQueryException(final String message) {
        super(message);
    }
}
