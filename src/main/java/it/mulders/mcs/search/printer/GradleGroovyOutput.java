package it.mulders.mcs.search.printer;

public final class GradleGroovyOutput implements CoordinatePrinter {

  @Override
  public String provideCoordinates(
      final String group, final String artifact, final String version, String packaging) {
    return "implementation group: '%s', name: '%s', version: '%s'"
        .formatted(group, artifact, version);
  }
}
