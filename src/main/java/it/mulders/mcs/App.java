package it.mulders.mcs;

import it.mulders.mcs.cli.Cli;
import it.mulders.mcs.cli.CommandClassFactory;
import it.mulders.mcs.cli.SystemPropertyLoader;
import it.mulders.mcs.common.McsExecutionExceptionHandler;
import java.net.URI;
import java.net.URISyntaxException;
import picocli.CommandLine;

public class App {
  public static void main(final String... args) {
    System.exit(doMain(args));
  }

  // Visible for testing
  static int doMain(final String... originalArgs) {
    return doMain(new Cli(), new SystemPropertyLoader(), originalArgs);
  }

  static int doMain(
      final Cli cli,
      final SystemPropertyLoader systemPropertyLoader,
      final String... originalArgs) {
    System.setProperties(systemPropertyLoader.getProperties());
    setUpProxy();
    var program =
        new CommandLine(cli, new CommandClassFactory(cli))
            .setExecutionExceptionHandler(new McsExecutionExceptionHandler());

    var args =
        isInvocationWithoutSearchCommand(program, originalArgs)
            ? prependSearchCommandToArgs(originalArgs)
            : originalArgs;

    return program.execute(args);
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
      System.err.println(
          "Error while setting up proxy from environment: HTTP_PROXY=[%s], HTTPS_PROXY=[%s]"
              .formatted(httpProxy, httpsProxy));
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
