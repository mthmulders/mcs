package it.mulders.mcs.printer;

public final class IvyXmlOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(
            final String group, final String artifact, final String version, String packaging) {
        return """
                <dependency org="%s" name="%s" rev="%s"/>
                """
                .formatted(group, artifact, version);
    }
}
