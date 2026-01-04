package com.bank.ui.gui;

import javax.swing.*;
import java.awt.*;

public class Router {
    private final JFrame frame;

    public Router(JFrame frame) { this.frame = frame; }

    public void setRoot(JPanel panel) {
        frame.getContentPane().removeAll();
        frame.getContentPane().add(panel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
}
