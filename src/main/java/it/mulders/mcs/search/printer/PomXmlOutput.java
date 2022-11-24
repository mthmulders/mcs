package it.mulders.mcs.search.printer;

public final class PomXmlOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(final String group, final String artifact, final String version) {
        return """
                    <dependency>
                        <groupId>%s</groupId>
                        <artifactId>%s</artifactId>
                        <version>%s</version>
                    </dependency>
                """.formatted(group, artifact, version);
    }
}
