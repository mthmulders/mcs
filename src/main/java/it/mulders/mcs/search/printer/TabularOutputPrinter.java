package it.mulders.mcs.search.printer;

import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.SearchResponse;
import it.mulders.mcs.search.vulnerability.ComponentReportResponse.ComponentReport;
import it.mulders.mcs.search.vulnerability.ComponentReportResponse.ComponentReport.ComponentReportVulnerability;
import it.mulders.mcs.search.vulnerability.ComponentReportVulnerabilitySeverity;
import java.io.PrintStream;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import picocli.CommandLine;
import picocli.CommandLine.Help;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Help.Column;
import picocli.CommandLine.Help.Column.Overflow;

public class TabularOutputPrinter implements OutputPrinter {
    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("dd MMM yyyy 'at' HH:mm (zzz)");
    private static final int INDENT = 2;
    private static final int SPACING = 3;

    private final boolean showVulnerabilities;

    public TabularOutputPrinter() {
        this(false);
    }

    public TabularOutputPrinter(final boolean showVulnerabilities) {
        this.showVulnerabilities = showVulnerabilities;
    }

    private String header(final SearchQuery query, final SearchResponse.Response response) {
        var numFound = response.numFound();
        var additionalMessage = numFound > query.searchLimit() ? " (showing %d)".formatted(response.docs().length) : "";
        return "Found @|bold %d|@ results%s%n".formatted(response.numFound(), additionalMessage);
    }

    public void print(final SearchQuery query, final SearchResponse.Response response, final PrintStream stream) {
        stream.println(CommandLine.Help.Ansi.AUTO.string(header(query, response)));

        var colorScheme = Help.defaultColorScheme(Ansi.AUTO);

        var table = CommandLine.Help.TextTable.forColumns(colorScheme, constructColumns(response));

        if (showVulnerabilities) {
            table.addRowValues("Coordinates", "Last updated", "Vulnerabilities");
            table.addRowValues("===========", "============", "===============");
        } else {
            table.addRowValues("Coordinates", "Last updated");
            table.addRowValues("===========", "============");
        }

        Arrays.stream(response.docs()).forEach(doc -> printRow(table, doc));

        stream.println(table);
    }

    private Column[] constructColumns(final SearchResponse.Response response) {
        var cols = new ArrayList<Column>();
        cols.add(new CommandLine.Help.Column(
                calculateCoordinateColumnWidth(response.docs()) + SPACING, INDENT, Overflow.SPAN));
        cols.add(new CommandLine.Help.Column(30, INDENT, Overflow.WRAP));
        if (showVulnerabilities) {
            cols.add(new CommandLine.Help.Column(50, INDENT, Overflow.SPAN));
        }
        return cols.toArray(Column[]::new);
    }

    private int calculateCoordinateColumnWidth(final SearchResponse.Response.Doc[] results) {
        return Arrays.stream(results)
                .map(this::displayEntry)
                .mapToInt(String::length)
                .max()
                .orElseThrow(() -> new IllegalStateException("Used TabularOutputPrinter without any output"));
    }

    private void printRow(final Help.TextTable table, final SearchResponse.Response.Doc doc) {
        var lastUpdated =
                DATE_TIME_FORMATTER.format(Instant.ofEpochMilli(doc.timestamp()).atZone(ZoneId.systemDefault()));

        var entry = displayEntry(doc);

        if (!showVulnerabilities) {
            table.addRowValues(entry, lastUpdated);
        } else {
            var vulnerabilityText = getVulnerabilityText(doc.componentReport());
            table.addRowValues(entry, lastUpdated, vulnerabilityText);
        }
    }

    private String getVulnerabilityText(ComponentReport componentReport) {
        if (componentReport == null || componentReport.vulnerabilities().length == 0) {
            return "-";
        }

        ComponentReportVulnerability[] sorted = componentReport.vulnerabilitiesSortedByCvssScore();

        Map<String, Long> counts = Arrays.stream(sorted)
                .map(vulnerability -> ComponentReportVulnerabilitySeverity.getText(vulnerability.cvssScore()))
                .collect(Collectors.groupingBy(Function.identity(), LinkedHashMap::new, Collectors.counting()));

        return counts.entrySet().stream()
                .map(entry -> entry.getValue() + " " + entry.getKey())
                .collect(Collectors.joining(", "));
    }

    private String displayEntry(final SearchResponse.Response.Doc doc) {
        if (doc.latestVersion() != null) {
            return doc.id() + ":" + doc.latestVersion();
        } else {
            return doc.id();
        }
    }
}
