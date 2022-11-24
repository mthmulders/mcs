package it.mulders.mcs.search.printer;

public class GradleGroovyShortOutput implements CoordinatesPrinter {

    @Override
    public String provideCoordinates(String group, String artifact, String version) {
        return "implementation '%s:%s:%s'".formatted(group, artifact, version);
    }
}
