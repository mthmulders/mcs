package it.mulders.mcs.search.printer;

public final class LeiningenOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(final String group, final String artifact, final String version) {
        return "[%s/%s \"%s\"]".formatted(group, artifact, version);
    }
}
