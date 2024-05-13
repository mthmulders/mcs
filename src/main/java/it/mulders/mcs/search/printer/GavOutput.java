package it.mulders.mcs.search.printer;

public final class GavOutput implements CoordinatePrinter {
    @Override
    public String provideCoordinates(
            final String group, final String artifact, final String version, String packaging) {
        if ("jar".equals(packaging)) return "%s:%s:%s".formatted(group, artifact, version);
        else return "%s:%s:%s@%s".formatted(group, artifact, version, packaging);
    }
}
