package it.mulders.mcs.search.printer;

import java.io.PrintStream;

import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.SearchResponse;
import it.mulders.mcs.search.vulnerability.ComponentReportResponse;

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
        if (componentReport != null) {
            String vulnerabilityText =  switch(componentReport.vulnerabilities().length) {
                case 0 -> "";
                case 1 -> "Found 1 vulnerability";
                default -> "Found %s vulnerabilities".formatted(componentReport.vulnerabilities().length);
            };
            stream.println(vulnerabilityText);
        }
    }
}
