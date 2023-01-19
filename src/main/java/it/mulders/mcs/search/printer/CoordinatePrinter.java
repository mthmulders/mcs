package it.mulders.mcs.search.printer;

import it.mulders.mcs.search.SearchQuery;
import it.mulders.mcs.search.SearchResponse;

import java.io.PrintStream;

public sealed interface CoordinatePrinter extends OutputPrinter
        permits BuildrOutput, GradleGroovyOutput, GradleGroovyShortOutput, GradleKotlinOutput, GrapeOutput,
        IvyXmlOutput, LeiningenOutput, PomXmlOutput, SbtOutput {

    String provideCoordinates(final String group, final String artifact, final String version);

    @Override
    default void print(final SearchQuery query, final SearchResponse.Response response, final PrintStream stream) {
        if (response.numFound() != 1) {
            throw new UnexpectedResultException("Search response with more than one result not expected here");
        }

        var doc = response.docs()[0];
        stream.println();
        stream.println(provideCoordinates(doc.g(), doc.a(), first(doc.v(), doc.latestVersion())));
        stream.println();
    }

    private String first(final String... values) {
        for (var value : values) {
            if (value != null) {
                return value;
            }
        }
        return null;
    }
}
