package it.mulders.mcs.dagger;

import dagger.Module;
import dagger.Provides;
import it.mulders.mcs.clipboard.Clipboard;
import it.mulders.mcs.clipboard.LinuxClipboard;
import it.mulders.mcs.clipboard.MacClipboard;
import it.mulders.mcs.clipboard.NoOpClipboard;
import it.mulders.mcs.clipboard.WindowsClipboard;
import java.util.Locale;

@Module
public interface ClipboardModule {
    @Provides
    static Clipboard provideClipboard() {
        var os = System.getProperty("os.name", "").toLowerCase(Locale.ROOT);
        if (os.contains("mac")) {
            return new MacClipboard();
        } else if (os.contains("win")) {
            return new WindowsClipboard();
        } else if (os.contains("nux") || os.contains("bsd")) {
            return new LinuxClipboard();
        } else {
            System.err.printf("Unsupported operating system: %s%n", os);
            return new NoOpClipboard();
        }
    }
}
