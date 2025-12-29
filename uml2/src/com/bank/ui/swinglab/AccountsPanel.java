package com.bank.ui.swinglab;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

class AccountsPanel extends JPanel implements ActionListener{
    private final JTable table;
    private final DefaultTableModel model;
    JButton details, clearSel, closePan;
    
    public AccountsPanel() {
        setLayout(new BorderLayout(8,8));
        setBorder(BorderFactory.createEmptyBorder(16,16,16,16));

        JLabel title = new JLabel("My Accounts");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        add(title, BorderLayout.NORTH);

        String[] cols = { "IBAN", "Type", "Balance (€)" };
        Object[][] data = {
            { "GR100000000000000001", "Personal", "1,245.50" },
            { "GR100000000000000002", "Personal", "3,010.00" },
            { "GR200000000000000001", "Business", "25,730.90" }
        };

        model = new DefaultTableModel(data, cols) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        table = new JTable(model);
        table.setFillsViewportHeight(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

        JPanel south = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        details = new JButton("View Details");   // <-- NEW
        clearSel = new JButton("Clear Selection");
        closePan = new JButton("Close");
        south.add(details);
        south.add(clearSel);
        south.add(closePan);
        add(south, BorderLayout.SOUTH);

        // events
        closePan.addActionListener(this);
        clearSel.addActionListener(this);
        details.addActionListener(this);
    }

    private void showSelectedRowDetails() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row first.", "No selection", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int modelRow = table.convertRowIndexToModel(viewRow);

        String iban    = String.valueOf(model.getValueAt(modelRow, 0));
        String type    = String.valueOf(model.getValueAt(modelRow, 1));
        String balance = String.valueOf(model.getValueAt(modelRow, 2));

        // ==== A) QUICK POPUP (JOptionPane) ====
        // String msg = "IBAN: " + iban + "\nType: " + type + "\nBalance: " + balance;
        // JOptionPane.showMessageDialog(this, msg, "Account Details", JOptionPane.INFORMATION_MESSAGE);

        // ==== B) NICE POPUP (JDialog) ====
        com.bank.ui.swinglab.AccountDetailsDialog dialog = new com.bank.ui.swinglab.AccountDetailsDialog(
                SwingUtilities.getWindowAncestor(this), iban, type, balance);
        dialog.setVisible(true);
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == closePan)
			AppMediator.getCardLayout().show(AppMediator.getCards(),"dashboard");
		else if (e.getSource() == clearSel)
			table.clearSelection();
		else if (e.getSource() == details)
			showSelectedRowDetails();
	}
}


