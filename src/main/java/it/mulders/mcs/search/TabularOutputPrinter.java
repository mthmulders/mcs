package it.mulders.mcs.search;

import picocli.CommandLine;
import picocli.CommandLine.Help;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Help.Column.Overflow;

import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class TabularOutputPrinter implements OutputPrinter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            "dd MMM yyyy 'at' HH:mm (zzz)"
    );
    private static final int INDENT = 2;
    private static final int SPACING = 3;

    private String header(final SearchQuery query, final SearchResponse.Response response) {
        var numFound = response.numFound();
        var additionalMessage = numFound > query.searchLimit()
                ? String.format(" (showing %d)", response.docs().length)
                : "";
        return String.format("Found @|bold %d|@ results%s%n",
                response.numFound(), additionalMessage);
    }

    public void print(final SearchQuery query, final SearchResponse.Response response, final PrintStream stream) {
        stream.println(CommandLine.Help.Ansi.AUTO.string(header(query, response)));

        var colorScheme = Help.defaultColorScheme(Ansi.AUTO);

        var maxKeyLength = calculateCoordinateColumnWidth(response.docs());

        var table = CommandLine.Help.TextTable.forColumns(colorScheme,
                new CommandLine.Help.Column(maxKeyLength + SPACING, INDENT, Overflow.SPAN),
                new CommandLine.Help.Column(30, INDENT, Overflow.WRAP)
        );

        table.addRowValues("Coordinates", "Last updated");
        table.addRowValues("===========", "============");
        Arrays.stream(response.docs()).forEach(doc -> printRow(table, doc));

        stream.println(table);
    }

    private int calculateCoordinateColumnWidth(final SearchResponse.Response.Doc[] results) {
        return Arrays.stream(results)
                .map(SearchResponse.Response.Doc::id)
                .mapToInt(String::length)
                .max()
                .orElseThrow(() -> new IllegalStateException("Used TabularOutputPrinter without any output"));
    }

    private void printRow(final Help.TextTable table, final SearchResponse.Response.Doc doc) {
        var lastUpdated = DATE_TIME_FORMATTER.format(
                Instant.ofEpochMilli(doc.timestamp()).atZone(ZoneId.systemDefault())
        );

        table.addRowValues(doc.id(), lastUpdated);
    }
}
