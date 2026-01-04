package com.bank.ui.gui.views;

import com.bank.ui.gui.controllers.DashboardController;
import com.bank.ui.gui.session.AppSession;
import com.bank.ui.gui.views.models.AccountsTableModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class AccountsPanel<User,Account,Transaction,Bill> extends JPanel implements PropertyChangeListener {
    private final AppSession<User,Account,Transaction,Bill> session;
    private final DashboardController<User,Account,Transaction,Bill> controller;

    private final AccountsTableModel<Account> tableModel = new AccountsTableModel<>();
    private final JTable table = new JTable(tableModel);

    private final JLabel selectedLabel = new JLabel("Selected: (none)");

    public AccountsPanel(AppSession<User,Account,Transaction,Bill> session,
                         DashboardController<User,Account,Transaction,Bill> controller) {
        this.session = session;
        this.controller = controller;

        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        // Header
        JLabel title = new JLabel("Accounts");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));

        JPanel top = new JPanel(new BorderLayout());
        top.add(title, BorderLayout.WEST);
        top.add(selectedLabel, BorderLayout.EAST);
        add(top, BorderLayout.NORTH);

        // Table polish
        table.setFillsViewportHeight(true);
        table.setRowHeight(26);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setAutoCreateRowSorter(true); // user can sort by clicking headers

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")));
        add(scroll, BorderLayout.CENTER);

        // Keep your existing behavior: selecting row -> controller.onSelectAccount(...)
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                Account selected = tableModel.getAt(table.getSelectedRow());
                controller.onSelectAccount(selected);

                selectedLabel.setText(selected == null ? "Selected: (none)" : "Selected: " + selected.toString());
            }
        });

        // Footer (optional refresh button; does not change logic)
        JButton refreshBtn = new JButton("Refresh");
        refreshBtn.addActionListener(e -> tableModel.setRows(session.getAccounts()));

        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        bottom.add(refreshBtn);
        add(bottom, BorderLayout.SOUTH);

        session.addListener(this);
        tableModel.setRows(session.getAccounts());

        // Optional: auto-select first row if exists (doesn't change flow, just UX)
        if (tableModel.getRowCount() > 0 && table.getSelectedRow() < 0) {
            table.setRowSelectionInterval(0, 0);
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (AppSession.EVT_ACCOUNTS.equals(evt.getPropertyName())) {
            tableModel.setRows(session.getAccounts());

            // keep selection if possible, else select first row for nicer UX
            if (tableModel.getRowCount() > 0 && table.getSelectedRow() < 0) {
                table.setRowSelectionInterval(0, 0);
            }
        }
    }
}
