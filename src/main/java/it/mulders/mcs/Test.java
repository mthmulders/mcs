package it.mulders.mcs;

import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

import java.util.concurrent.Callable;

@Command(name = "test")
public class Test implements Callable<Integer> {
    @Parameters(
            arity = "1",
            description = "Whom to greet"
    )
    private String name;

    @Override
    public Integer call() {
        System.out.printf("Howdy, %s!%n", name);
        return 0;
    }
}
