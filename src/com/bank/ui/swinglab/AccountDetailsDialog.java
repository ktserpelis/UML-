package com.bank.ui.swinglab;
import java.awt.*;

import javax.swing.*;;

// Popup Using JDialog
class AccountDetailsDialog extends JDialog {
    AccountDetailsDialog(Window owner, String iban, String type, String balance) {
        super(owner, "Account Details", ModalityType.APPLICATION_MODAL);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        JPanel content = new JPanel(new GridBagLayout());
        content.setBorder(BorderFactory.createEmptyBorder(16,16,16,16));
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.anchor = GridBagConstraints.WEST;

        // Row 0
        c.gridx = 0; c.gridy = 0; content.add(new JLabel("IBAN:"), c);
        c.gridx = 1; c.gridy = 0; content.add(new JTextField(iban, 22) {{
            setEditable(false);
        }}, c);

        // Row 1
        c.gridx = 0; c.gridy = 1; content.add(new JLabel("Type:"), c);
        c.gridx = 1; c.gridy = 1; content.add(new JTextField(type, 22) {{
            setEditable(false);
        }}, c);

        // Row 2
        c.gridx = 0; c.gridy = 2; content.add(new JLabel("Balance (€):"), c);
        c.gridx = 1; c.gridy = 2; content.add(new JTextField(balance, 22) {{
            setEditable(false);
        }}, c);

        // Buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton close = new JButton("Close");
        buttons.add(close);

        c.gridwidth = 2;
        c.gridx = 0; c.gridy = 3; c.anchor = GridBagConstraints.EAST;
        content.add(buttons, c);

        setContentPane(content);
        pack();
        setLocationRelativeTo(owner);

        close.addActionListener(e -> dispose());
    }
}

