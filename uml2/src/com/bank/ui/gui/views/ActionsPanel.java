package com.bank.ui.gui.views;

import com.bank.ui.gui.controllers.DashboardController;

import javax.swing.*;
import java.awt.*;

public class ActionsPanel<User,Account,Transaction,Bill> extends JPanel {
    public ActionsPanel(DashboardController<User,Account,Transaction,Bill> controller, boolean isCompany) {
        setLayout(new GridLayout(0, 1, 8, 8));

        JTextField dep = new JTextField();
        JButton depBtn = new JButton("Deposit");
        depBtn.addActionListener(e -> controller.onDeposit(dep.getText()));

        JTextField w = new JTextField();
        JButton wBtn = new JButton("Withdraw");
        wBtn.addActionListener(e -> controller.onWithdraw(w.getText()));

        JTextField iban = new JTextField();
        JTextField amt = new JTextField();
        JButton trBtn = new JButton("Transfer");
        trBtn.addActionListener(e -> controller.onTransfer(iban.getText(), amt.getText()));

        JTextField rf = new JTextField();
        JButton payBtn = new JButton("Pay RF Bill");
        payBtn.addActionListener(e -> controller.onPayRf(rf.getText()));

        add(new JLabel("Deposit amount:")); add(dep); add(depBtn);
        add(new JLabel("Withdraw amount:")); add(w); add(wBtn);
        add(new JLabel("Transfer to IBAN:")); add(iban);
        add(new JLabel("Transfer amount:")); add(amt); add(trBtn);
        add(new JLabel("RF Code:")); add(rf); add(payBtn);

        if (isCompany) {
            JButton load = new JButton("Load Issued Bills (Company)");
            load.addActionListener(e -> controller.onLoadIssuedBills());
            add(load);
        }
    }
}
