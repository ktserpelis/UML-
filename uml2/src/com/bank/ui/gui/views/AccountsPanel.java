package com.bank.ui.gui.views;

import com.bank.ui.gui.controllers.DashboardController;
import com.bank.ui.gui.session.AppSession;
import com.bank.ui.gui.views.models.AccountsTableModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class AccountsPanel<User,Account,Transaction,Bill> extends JPanel implements PropertyChangeListener {
    private final AppSession<User,Account,Transaction,Bill> session;
    private final DashboardController<User,Account,Transaction,Bill> controller;

    private final AccountsTableModel<Account> tableModel = new AccountsTableModel<>();
    private final JTable table = new JTable(tableModel);

    public AccountsPanel(AppSession<User,Account,Transaction,Bill> session, DashboardController<User,Account,Transaction,Bill> controller) {
        this.session = session; this.controller = controller;

        setLayout(new BorderLayout());
        add(new JScrollPane(table), BorderLayout.CENTER);

        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() >= 0) {
                controller.onSelectAccount(tableModel.getAt(table.getSelectedRow()));
            }
        });

        session.addListener(this);
        tableModel.setRows(session.getAccounts());
    }

    @Override public void propertyChange(PropertyChangeEvent evt) {
        if (AppSession.EVT_ACCOUNTS.equals(evt.getPropertyName())) {
            tableModel.setRows(session.getAccounts());
        }
    }
}
