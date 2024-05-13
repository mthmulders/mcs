package it.mulders.mcs.cli;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * Loads additional system properties from a predefined file on disk.
 * <br />
 * Copies those properties over the existing ones @{{@link System#getProperties()}} so the result
 * is a drop-in replacement that could be passed for {@link System#setProperties(Properties)}.
 * This class does not modify the System properties itself.
 */
public class SystemPropertyLoader {
    private static final Path MCS_PROPERTIES_FILE = Paths.get(System.getProperty("user.home"), ".mcs", "mcs.config");

    private final Properties properties = new Properties();
    ;

    public SystemPropertyLoader() {
        this(MCS_PROPERTIES_FILE);
    }

    protected SystemPropertyLoader(final Path source) {
        properties.putAll(System.getProperties());

        if (Files.exists(source) && Files.isRegularFile(source)) {
            var input = new Properties();
            try (var reader = Files.newBufferedReader(source)) {
                input.load(reader);
                properties.putAll(input);
            } catch (IOException ioe) {
                System.err.printf("Failed to load %s: %s%n", source, ioe.getLocalizedMessage());
            }
        }
    }

    public Properties getProperties() {
        return properties;
    }
}
