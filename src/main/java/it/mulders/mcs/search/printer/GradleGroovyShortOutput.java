package it.mulders.mcs.search.printer;

public final class GradleGroovyShortOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(
            final String group, final String artifact, final String version, String packaging) {
        return "implementation '%s:%s:%s'".formatted(group, artifact, version);
    }
}
