package com.bank.ui.gui.controllers;

import javax.swing.*;
import java.io.OutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class TextAreaOutputStream extends OutputStream {
    private final JTextArea textArea;

    public TextAreaOutputStream(JTextArea textArea) {
        this.textArea = textArea;
    }

    @Override
    public void write(int b) throws IOException {
        SwingUtilities.invokeLater(() -> {
            textArea.append(String.valueOf((char) b));
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        String s = new String(b, off, len, StandardCharsets.UTF_8);
        SwingUtilities.invokeLater(() -> {
            textArea.append(s);
            textArea.setCaretPosition(textArea.getDocument().getLength());
        });
    }
}