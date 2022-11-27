package it.mulders.mcs.search.printer;

public final class GradleGroovyShortOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(String group, String artifact, String version) {
        return "implementation '%s:%s:%s'".formatted(group, artifact, version);
    }
}
