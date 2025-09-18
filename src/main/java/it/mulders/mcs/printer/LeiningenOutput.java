package it.mulders.mcs.printer;

public final class LeiningenOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(
            final String group, final String artifact, final String version, String packaging) {
        return "[%s/%s \"%s\"]".formatted(group, artifact, version);
    }
}
