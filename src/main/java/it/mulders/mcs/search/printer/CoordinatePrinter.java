package it.mulders.mcs.search.printer;

import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.SearchResponse;
import it.mulders.mcs.search.vulnerability.ComponentReportResponse;
import it.mulders.mcs.search.vulnerability.ComponentReportResponse.ComponentReport.ComponentReportVulnerability;

import java.io.PrintStream;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public sealed interface CoordinatePrinter extends OutputPrinter
        permits BuildrOutput, GradleGroovyOutput, GradleGroovyShortOutput, GradleKotlinOutput, GrapeOutput,
        IvyXmlOutput, LeiningenOutput, PomXmlOutput, SbtOutput {

    String provideCoordinates(final String group, final String artifact, final String version, final String packaging);

    @Override
    default void print(final SearchQuery query, final SearchResponse.Response response, final PrintStream stream) {
        if (response.numFound() != 1) {
            throw new IllegalArgumentException("Search response with more than one result not expected here");
        }

        var doc = response.docs()[0];
        stream.println();
        stream.println(provideCoordinates(doc.g(), doc.a(), first(doc.v(), doc.latestVersion()), doc.p()));
        stream.println();
        printVulnerabilities(doc.componentReport(), stream);
    }

    private String first(final String... values) {
        for (var value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }

    private void printVulnerabilities(final ComponentReportResponse.ComponentReport componentReport,
                                      final PrintStream stream) {
        if (componentReport != null && componentReport.vulnerabilities().length > 0) {
            var text = "Found %s vulnerabilities %s"
                .formatted(
                    componentReport.vulnerabilities().length,
                    Stream.of(componentReport.vulnerabilities())
                        .map(ComponentReportVulnerability::id)
                        .collect(Collectors.joining(", ", "(", ")"))
                );
            stream.println(text);
            stream.println("Reference: " + componentReport.reference());
        }
    }
}
