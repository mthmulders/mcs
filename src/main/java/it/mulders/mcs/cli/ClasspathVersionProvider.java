package it.mulders.mcs.cli;

import java.util.Properties;
import picocli.CommandLine;

/**
 * {@link CommandLine.IVersionProvider} implementation that returns version information from a
 * {@code /mcs.properties} file in the classpath.
 */
public class ClasspathVersionProvider implements CommandLine.IVersionProvider {
  @Override
  public String[] getVersion() throws Exception {
    var properties = new Properties();
    try (var stream = getClass().getResourceAsStream("/mcs.properties")) {
      properties.load(stream);
      var version = String.format("mcs v%s", properties.getProperty("mcs.version"));
      return new String[] {version};
    }
  }
}
