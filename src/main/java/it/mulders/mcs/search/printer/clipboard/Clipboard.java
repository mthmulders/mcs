package it.mulders.mcs.search.printer.clipboard;

public sealed interface Clipboard permits SystemClipboard {
    void copy(String text);
    String paste();
}

