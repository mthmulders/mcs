package it.mulders.mcs.search.printer;

public final class IvyXmlOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(String group, String artifact, String version) {
        return """
                <dependency org="%s" name="%s" rev="%s"/>
                """.formatted(group, artifact, version);
    }
}
