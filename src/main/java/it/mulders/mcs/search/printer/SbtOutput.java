package it.mulders.mcs.search.printer;

public class SbtOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(String group, String artifact, String version) {
        return """
                libraryDependencies += "%s" %% "%s" %% "%s"
                """.formatted(group, artifact, version);
    }
}
