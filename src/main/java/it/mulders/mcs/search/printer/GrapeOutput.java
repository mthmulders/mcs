package it.mulders.mcs.search.printer;

public final class GrapeOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(String group, String artifact, String version) {
        return """
                @Grapes(
                    @Grab(group='%s', module='%s', version='%s')
                )
                """.formatted(group, artifact, version);
    }
}
