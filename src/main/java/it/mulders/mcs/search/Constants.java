package it.mulders.mcs.search;

import it.mulders.mcs.search.printer.CoordinatePrinter;
import it.mulders.mcs.search.printer.PomXmlOutput;

public class Constants {
    public static final Integer DEFAULT_MAX_SEARCH_RESULTS = 20;
    public static final Integer DEFAULT_START = 0;
    public static final Integer MAX_LIMIT = 200;
    public static final CoordinatePrinter DEFAULT_PRINTER = new PomXmlOutput();

    private Constants() {
    }
}
