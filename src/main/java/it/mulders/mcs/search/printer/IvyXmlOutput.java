package it.mulders.mcs.search.printer;

public final class IvyXmlOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(final String group, final String artifact, final String version) {
        return """
                <dependency org="%s" name="%s" rev="%s"/>
                """.formatted(group, artifact, version);
    }
}
