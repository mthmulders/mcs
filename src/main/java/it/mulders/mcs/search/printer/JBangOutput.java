package it.mulders.mcs.search.printer;

public final class JBangOutput implements CoordinatePrinter {
  @Override
  public String provideCoordinates(
      final String group, final String artifact, final String version, String packaging) {
    if ("jar".equals(packaging)) return "//DEPS %s:%s:%s".formatted(group, artifact, version);
    else return "//DEPS %s:%s:%s@%s".formatted(group, artifact, version, packaging);
  }
}
