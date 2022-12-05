package it.mulders.mcs.search.printer;

public final class BuildrOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(final String group, final String artifact, final String version) {
        return "'%s:%s:jar:%s'".formatted(group, artifact, version);
    }
}
