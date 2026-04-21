package it.mulders.mcs.printer;

public final class UrlOutput implements CoordinatePrinter {
    private static final String MAVEN_CENTRAL = "https://repo.maven.apache.org/maven2";

    @Override
    public String provideCoordinates(
            final String group, final String artifact, final String version, String packaging) {
        return "%s/%s/%s/%s/%s-%s.%s"
                .formatted(
                        MAVEN_CENTRAL,
                        group.replace('.', '/'),
                        artifact,
                        version,
                        artifact,
                        version,
                        toFileExtension(packaging));
    }

    private static String toFileExtension(String packaging) {
        return switch (packaging) {
            case "maven-plugin", "bundle" -> "jar";
            default -> packaging;
        };
    }
}
