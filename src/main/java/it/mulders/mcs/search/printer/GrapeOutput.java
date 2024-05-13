package it.mulders.mcs.search.printer;

public final class GrapeOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(
            final String group, final String artifact, final String version, String packaging) {
        return """
                @Grapes(
                    @Grab(group='%s', module='%s', version='%s')
                )
                """
                .formatted(group, artifact, version);
    }
}
