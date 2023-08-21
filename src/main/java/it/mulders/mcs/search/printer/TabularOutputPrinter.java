package it.mulders.mcs.search.printer;

import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.SearchResponse;
import it.mulders.mcs.search.vulnerability.ComponentReportResponse.ComponentReport.ComponentReportVulnerability;
import picocli.CommandLine;
import picocli.CommandLine.Help;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Help.Column.Overflow;

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
                new CommandLine.Help.Column(30, INDENT, Overflow.WRAP),
                new CommandLine.Help.Column(300, INDENT, Overflow.SPAN)
        );

        table.addRowValues("Coordinates", "Last updated", "Vulnerabilities");
        table.addRowValues("===========", "============", "===============");
        Arrays.stream(response.docs()).forEach(doc -> printRow(table, doc));

        stream.println(table);
    }

    private int calculateCoordinateColumnWidth(final SearchResponse.Response.Doc[] results) {
        return Arrays.stream(results)
                .map(this::displayEntry)
                .mapToInt(String::length)
                .max()
                .orElseThrow(() -> new IllegalStateException("Used TabularOutputPrinter without any output"));
    }

    private void printRow(final Help.TextTable table, final SearchResponse.Response.Doc doc) {
       var vulnerabilities = "";
       if (doc.componentReport() != null) {
            var numVuls = doc.componentReport().vulnerabilities().length;
            if (numVuls > 0) {
              vulnerabilities += "Found " + numVuls + " vulnerabilities ";
              //vulnerabilities += doc.componentReport().reference();
              vulnerabilities += Stream.of(doc.componentReport().vulnerabilities())
                  .map(ComponentReportVulnerability::id).collect(Collectors.joining(", ", "(", ")"));
            }

       }

        var lastUpdated = DATE_TIME_FORMATTER.format(
                Instant.ofEpochMilli(doc.timestamp()).atZone(ZoneId.systemDefault())
        );

        var entry = displayEntry(doc);

        if (vulnerabilities != "") {
            table.addRowValues(entry, lastUpdated, vulnerabilities);
        } else {
            table.addRowValues(entry, lastUpdated);
        }
    }

    private String displayEntry(final SearchResponse.Response.Doc doc) {
        if (doc.latestVersion() != null) {
            return doc.id() + ":" + doc.latestVersion();
        } else {
            return doc.id();
        }
    }
}
