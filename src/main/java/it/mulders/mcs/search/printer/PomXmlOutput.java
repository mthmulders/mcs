package it.mulders.mcs.search.printer;

public class PomXmlOutput implements CoordinatesPrinter {

    @Override
    public String provideCoordinates(String group, String artifact, String version) {
        return """
                    <dependency>
                        <groupId>%s</groupId>
                        <artifactId>%s</artifactId>
                        <version>%s</version>
                    </dependency>
                """.formatted(group, artifact, version);
    }
}
