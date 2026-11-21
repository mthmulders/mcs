package it.mulders.mcs;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestUtils {
    public static String getResourceAsString(final String resourceName) throws IOException {
        try (final InputStream input = TestUtils.class.getResourceAsStream(resourceName)) {
            byte[] bytes = input != null ? input.readAllBytes() : new byte[] {};
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }
}
