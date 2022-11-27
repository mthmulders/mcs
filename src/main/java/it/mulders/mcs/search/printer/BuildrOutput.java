package it.mulders.mcs.search.printer;

public final class BuildrOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(String group, String artifact, String version) {
        return "'%s:%s:jar:%s'".formatted(group, artifact, version);
    }
}
