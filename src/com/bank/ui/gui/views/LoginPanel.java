package com.bank.ui.gui.views;

import com.bank.ui.gui.controllers.LoginController;

import javax.swing.*;
import java.awt.*;

public class LoginPanel extends JPanel {

    private final JTextField userField = new JTextField(20);
    private final JPasswordField passField = new JPasswordField(20);

    private final JButton btnClear = new JButton("Clear");
    private final JButton btnLogin = new JButton("Sign in");

    public LoginPanel(LoginController controller) {
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        setLayout(new GridBagLayout());

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;

        // Row 0: Username
        c.gridx = 0; c.gridy = 0;
        add(new JLabel("Username:"), c);

        c.gridx = 1; c.gridy = 0;
        add(userField, c);

        // Row 1: Password
        c.gridx = 0; c.gridy = 1;
        add(new JLabel("Password:"), c);

        c.gridx = 1; c.gridy = 1;
        add(passField, c);

        // Row 2: Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnClear);
        buttons.add(btnLogin);

        c.gridwidth = 2;
        c.gridx = 0; c.gridy = 2;
        add(buttons, c);

        // Events
        btnClear.addActionListener(e -> {
            userField.setText("");
            passField.setText("");
            userField.requestFocusInWindow();
        });

        btnLogin.addActionListener(e ->
                controller.onLogin(
                        userField.getText(),
                        new String(passField.getPassword())
                )
        );

        // Optional: press Enter in password field triggers login
        passField.addActionListener(e -> btnLogin.doClick());
    }
}
