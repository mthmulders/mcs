package it.mulders.mcs.cli;

import picocli.CommandLine;

/**
 * Implementation of the {@link CommandLine.IFactory} interface that can construct instances of the
 * {@link Cli} nested command classes. Since these classes get their dependencies from their parent
 * class, they cannot be static classes.
 */
public class CommandClassFactory implements CommandLine.IFactory {
  private final CommandLine.IFactory defaultFactory = CommandLine.defaultFactory();
  private final Cli cli;

  public CommandClassFactory(final Cli cli) {
    this.cli = cli;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <K> K create(Class<K> cls) throws Exception {
    if (cls == Cli.SearchCommand.class) {
      return (K) cli.createSearchCommand();
    } else if (cls == Cli.ClassSearchCommand.class) {
      return (K) cli.createClassSearchCommand();
    }

    return defaultFactory.create(cls);
  }
}
