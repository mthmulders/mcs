package it.mulders.mcs.search.printer;

public final class SbtOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(final String group, final String artifact, final String version) {
        return """
                libraryDependencies += "%s" %% "%s" %% "%s"
                """.formatted(group, artifact, version);
    }
}
