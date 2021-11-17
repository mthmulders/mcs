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
import java.util.Collection;

public class TabularOutputPrinter implements OutputPrinter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern(
            "dd MMM yyyy 'at' HH:mm (zzz)"
    );
    private static final int INDENT = 2;
    private static final int MAX_LINE_LENGTH = 120;
    private static final int SPACING = 3;

    public void print(final SearchResponse.Response response, final PrintStream stream) {
        var message = String.format("Found @|bold %d|@ results%n", response.numFound());
        stream.println(CommandLine.Help.Ansi.AUTO.string(message));

        var colorScheme = Help.defaultColorScheme(Ansi.AUTO);

        var maxKeyLength = maxLength(Arrays.stream(response.docs()).map(this::gav).toList());

        var table = CommandLine.Help.TextTable.forColumns(colorScheme,
                new CommandLine.Help.Column(maxKeyLength + SPACING, INDENT, Overflow.SPAN),
                new CommandLine.Help.Column(MAX_LINE_LENGTH - (maxKeyLength + SPACING), INDENT, Overflow.WRAP)
        );

        table.addRowValues("Coordinates", "Last updated");
        table.addRowValues("===========", "============");
        Arrays.stream(response.docs()).forEach(doc -> printRow(table, doc));

        stream.println(table);
    }

    private String gav(final SearchResponse.Response.Doc doc) {
        return String.format("%s:%s", doc.id(), doc.latestVersion());
    }

    private void printRow(final Help.TextTable table, final SearchResponse.Response.Doc doc) {
        var lastUpdated = DATE_TIME_FORMATTER.format(
                Instant.ofEpochMilli(doc.timestamp()).atZone(ZoneId.systemDefault())
        );

        table.addRowValues(gav(doc), lastUpdated);
    }

    private static int maxLength(Collection<?> any) {
        int result = 0;
        for (Object value : any) { result = Math.max(result, String.valueOf(value).length()); }
        return result;
    }
}
