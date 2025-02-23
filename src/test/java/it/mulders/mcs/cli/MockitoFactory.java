package it.mulders.mcs.cli;

import org.mockito.Mockito;
import picocli.CommandLine;

public class MockitoFactory implements CommandLine.IFactory {
    public static final CommandLine.IFactory INSTANCE = new MockitoFactory();

    @Override
    public <K> K create(Class<K> cls) throws Exception {
        return Mockito.mock(cls);
    }
}
