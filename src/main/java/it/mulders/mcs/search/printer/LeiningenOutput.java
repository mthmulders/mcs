package it.mulders.mcs.search.printer;

public final class LeiningenOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(String group, String artifact, String version) {
        return "[%s/%s \"%s\"]".formatted(group, artifact, version);
    }
}
