package it.mulders.mcs.search.printer.clipboard;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

import static java.awt.datatransfer.DataFlavor.stringFlavor;

public final class SystemClipboard implements Clipboard {
    java.awt.datatransfer.Clipboard systemClipboard;

    public SystemClipboard() {
        this.systemClipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
    }

    @Override
    public void copy(String text) {
        var clipboardData = new StringSelection(text);
        systemClipboard.setContents(clipboardData, clipboardData);
    }

    @Override
    public String paste() {
        Transferable clipboardData = systemClipboard.getContents(null);
        String text = "";
        boolean hasTransferableText = clipboardData != null && clipboardData.isDataFlavorSupported(stringFlavor);

        if (hasTransferableText) {
            try {
                text = (String) clipboardData.getTransferData(stringFlavor);
            } catch (IOException | UnsupportedFlavorException e) {
                throw new RuntimeException(e);
            }
        }

        return text;
    }
}