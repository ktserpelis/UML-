package com.bank.ui.gui.views;

import com.bank.ui.gui.controllers.DashboardController;
import com.bank.ui.gui.session.AppSession;

import javax.swing.*;
import java.awt.*;

public class DashboardPanel<User,Account,Transaction,Bill> extends JPanel {
    public DashboardPanel(AppSession<User,Account,Transaction,Bill> session,
                          DashboardController<User,Account,Transaction,Bill> controller,
                          boolean isCompany, boolean isAdmin) {

        setLayout(new BorderLayout(10,10));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel header = new JLabel("Dashboard");
        headerPanel.add(header, BorderLayout.WEST);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> controller.onLogout());
        headerPanel.add(logoutBtn, BorderLayout.EAST);

        add(headerPanel, BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Accounts", new AccountsPanel<>(session, controller));
        tabs.addTab("Transactions", new TransactionsPanel<>(session));
        tabs.addTab(isCompany ? "Issued Bills" : "Outstanding Bills", new BillsPanel<>(session));

        tabs.addTab("Standing Orders", new StandingOrdersPanel<>(controller));

        tabs.addTab("Actions", new ActionsPanel<>(controller, isCompany, isAdmin));

        if (isAdmin) {
            tabs.addTab("Admin", new AdminPanel<>(controller));
        }

        if (isCompany) {
            tabs.addTab("Create Bill", new CreateBillPanel<>(controller));
        }


        add(tabs, BorderLayout.CENTER);
    }
}
