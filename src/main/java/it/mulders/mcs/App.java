package it.mulders.mcs;

import it.mulders.mcs.dagger.Application;
import it.mulders.mcs.dagger.DaggerApplication;
import java.net.URI;
import java.net.URISyntaxException;
import picocli.CommandLine;

public class App {
    public static void main(final String... args) {
        System.exit(doMain(args));
    }

    // Visible for testing
    static int doMain(final String... originalArgs) {
        final Application components = DaggerApplication.create();

        var systemPropertyLoader = components.systemPropertyLoader();
        System.setProperties(systemPropertyLoader.getProperties());
        setUpProxy();

        var commandLine = components.commandLine();

        var args = isInvocationWithoutSearchCommand(commandLine, originalArgs)
                ? prependSearchCommandToArgs(originalArgs)
                : originalArgs;

        return commandLine.execute(args);
    }

    private static void setUpProxy() {
        var httpProxy = System.getenv("HTTP_PROXY");
        var httpsProxy = System.getenv("HTTPS_PROXY");

        try {
            if (httpProxy != null && !httpProxy.isEmpty()) {
                final URI uri = new URI(httpProxy);

                System.setProperty("http.proxyHost", uri.getHost());
                System.setProperty("http.proxyPort", Integer.toString(uri.getPort()));
            }

            if (httpsProxy != null && !httpsProxy.isEmpty()) {
                final URI uri = new URI(httpsProxy);
                System.setProperty("https.proxyHost", uri.getHost());
                System.setProperty("https.proxyPort", Integer.toString(uri.getPort()));
            }
        } catch (URISyntaxException e) {
            System.err.printf(
                    "Error while setting up proxy from environment: HTTP_PROXY=[%s], HTTPS_PROXY=[%s]%n",
                    httpProxy, httpsProxy);
        }
    }

    static boolean isInvocationWithoutSearchCommand(CommandLine program, String... args) {
        try {
            program.parseArgs(args);
            return false;
        } catch (CommandLine.ParameterException pe1) {
            try {
                program.parseArgs(prependSearchCommandToArgs(args));
                return true;
            } catch (CommandLine.ParameterException pe2) {
                return false;
            }
        }
    }

    static String[] prependSearchCommandToArgs(String... originalArgs) {
        var args = new String[originalArgs.length + 1];
        args[0] = "search";
        System.arraycopy(originalArgs, 0, args, 1, originalArgs.length);

        return args;
    }
}
