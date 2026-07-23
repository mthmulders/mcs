package it.mulders.mcs.clipboard;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

final class Shell {
    Shell() {}

    boolean which(final String command) {
        try {
            return new ProcessBuilder("which", command).start().waitFor() == 0;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (IOException e) {
            return false;
        }
    }

    boolean run(final String command, final String stdin) {
        return run(List.of(command), stdin);
    }

    boolean run(final List<String> command, final String stdin) {
        try {
            var process = new ProcessBuilder(command).start();
            try (var out = process.getOutputStream()) {
                out.write(stdin.getBytes(StandardCharsets.UTF_8));
            }

            if (!process.waitFor(Duration.ofSeconds(3))) {
                process.destroyForcibly();
                return false;
            }
            return process.exitValue() == 0;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        } catch (IOException e) {
            return false;
        }
    }
}
