package it.mulders.mcs.common;

import jakarta.inject.Inject;
import picocli.CommandLine;

public class McsExecutionExceptionHandler implements CommandLine.IExecutionExceptionHandler {
    @Inject
    public McsExecutionExceptionHandler() {}

    @Override
    public int handleExecutionException(Exception ex, CommandLine commandLine, CommandLine.ParseResult parseResult) {
        var message =
                ex instanceof McsRuntimeException ? ex.getCause().getLocalizedMessage() : ex.getLocalizedMessage();
        System.err.printf("MCS ran into an error: %s%n", message);
        System.err.printf("%n");
        System.err.printf(
                "If the error persist, please consider reporting the issue at https://github.com/mthmulders/mcs/issues/new%n");
        System.err.printf("%n");
        return -1;
    }
}
