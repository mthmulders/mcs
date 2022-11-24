package it.mulders.mcs.search;

public enum OutputType {
    MAVEN("maven"),
    GRADLE("gradle"),
    GRADLE_SHORT("gradle-short");

    private final String label;

    OutputType(String outputType) {
        this.label = outputType;
    }

    public String getLabel() {
        return label;
    }
}
