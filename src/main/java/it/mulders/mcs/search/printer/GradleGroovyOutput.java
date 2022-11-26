package it.mulders.mcs.search.printer;

public class GradleGroovyOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(String group, String artifact, String version) {
        return "implementation group: '%s', name: '%s', version: '%s'".formatted(group, artifact, version);
    }
}
