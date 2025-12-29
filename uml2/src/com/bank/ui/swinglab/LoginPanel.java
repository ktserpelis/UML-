package com.bank.ui.swinglab;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;

class LoginPanel extends JPanel implements ActionListener {
    private final JTextField userField = new JTextField(14);
    private final JPasswordField passField = new JPasswordField(14);
    private final JButton btnLogin = new JButton("Login");
    private final JButton btnClear = new JButton("Clear");

    // onSuccess switches to dashboard
    LoginPanel() {
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;

        c.gridx = 0; c.gridy = 0; add(new JLabel("Username:"), c);
        c.gridx = 1; c.gridy = 0; add(userField, c);

        c.gridx = 0; c.gridy = 1; add(new JLabel("Password:"), c);
        c.gridx = 1; c.gridy = 1; add(passField, c);

        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(btnClear);
        buttons.add(btnLogin);

        c.gridwidth = 2;
        c.gridx = 0; c.gridy = 2; add(buttons, c);

        // events
        btnClear.addActionListener(this);
        btnLogin.addActionListener(this);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == btnLogin) {
			String user = userField.getText().trim();
            if (user.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a username.", "Missing info", JOptionPane.WARNING_MESSAGE);
                return;
            }
            JOptionPane.showMessageDialog(this, "Welcome, " + user + "!");
            AppMediator.setUser_id(111);
            AppMediator.getBank().enableUserMenu();
            AppMediator.getCardLayout().show(AppMediator.getCards(), "dashboard");
		} else if (e.getSource() == btnClear) {
			userField.setText("");
            passField.setText("");
            userField.requestFocusInWindow();
		}
	}
}

