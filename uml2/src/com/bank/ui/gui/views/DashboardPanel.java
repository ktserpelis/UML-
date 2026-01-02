package com.bank.ui.gui.views;

import com.bank.ui.gui.controllers.DashboardController;
import com.bank.ui.gui.session.AppSession;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel<User,Account,Transaction,Bill> extends JPanel {
    public DashboardPanel(AppSession<User,Account,Transaction,Bill> session,
                          DashboardController<User,Account,Transaction,Bill> controller,
                          boolean isCompany) {

        setLayout(new BorderLayout(10,10));

        JLabel header = new JLabel("Dashboard");
        add(header, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Accounts", new AccountsPanel<>(session, controller));
        tabs.addTab("Transactions", new TransactionsPanel<>(session));
        tabs.addTab(isCompany ? "Issued Bills" : "Outstanding Bills", new BillsPanel<>(session));
        tabs.addTab("Actions", new ActionsPanel<>(controller, isCompany));

        add(tabs, BorderLayout.CENTER);
    }
}
