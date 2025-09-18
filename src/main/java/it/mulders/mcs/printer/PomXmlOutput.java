package it.mulders.mcs.printer;

public final class PomXmlOutput implements CoordinatePrinter {

    @Override
    public String provideCoordinates(
            final String group, final String artifact, final String version, String packaging) {
        String element = "maven-plugin".equals(packaging) ? "plugin" : "dependency";
        return """
                    <%4$s>
                        <groupId>%s</groupId>
                        <artifactId>%s</artifactId>
                        <version>%s</version>
                    </%4$s>
                """
                .formatted(group, artifact, version, element);
    }
}
