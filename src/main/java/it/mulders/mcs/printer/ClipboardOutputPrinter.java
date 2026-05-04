package it.mulders.mcs.printer;

import it.mulders.mcs.clipboard.Clipboard;
import it.mulders.mcs.search.artifact.SearchQuery;
import it.mulders.mcs.search.artifact.SearchResponse;
import java.io.PrintStream;

/**
 * Decorates a {@link CoordinatePrinter} with logic to copy the coordinate snippet to the clipboard after it has been printed.
 */
public final class ClipboardOutputPrinter implements OutputPrinter {
    private final OutputPrinter delegate;
    private final Clipboard clipboard;
    private final boolean copy;

    public ClipboardOutputPrinter(final OutputPrinter delegate, final Clipboard clipboard, final boolean copy) {
        this.delegate = delegate;
        this.clipboard = clipboard;
        this.copy = copy;
    }

    @Override
    public void print(final SearchQuery query, final SearchResponse.Response response, final PrintStream stream) {
        delegate.print(query, response, stream);

        if (copy) {
            if (delegate instanceof CoordinatePrinter cp) {
                var coordinates = cp.provideCoordinates(response.docs()[0]);
                stream.println(
                        clipboard.copy(coordinates) ? "Snippet copied to clipboard." : "Could not copy to clipboard.");
            }
        } else {
            stream.println("Pass -c or --copy to copy this snippet to your clipboard.");
        }
    }
}
