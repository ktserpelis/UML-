package com.bank.ui.gui.views;

import com.bank.ui.gui.controllers.DashboardController;
import com.bank.ui.gui.errors.ErrorBus;

import javax.swing.*;
import java.awt.*;

public class ActionsPanel<User,Account,Transaction,Bill> extends JPanel {

    public ActionsPanel(DashboardController<User,Account,Transaction,Bill> controller,
                        boolean isCompany,
                        boolean isAdmin) {

        setLayout(new GridLayout(0, 2, 8, 8));

        // Deposit
        JTextField dep = new JTextField();
        JButton depBtn = new JButton("Deposit");
        depBtn.addActionListener(e -> {
            ErrorBus.getInstance().resetLastError();
            controller.onDeposit(dep.getText());
            if (!ErrorBus.getInstance().hadError()) dep.setText("");
        });

        // Withdraw
        JTextField w = new JTextField();
        JButton wBtn = new JButton("Withdraw");
        wBtn.addActionListener(e -> {
            ErrorBus.getInstance().resetLastError();
            controller.onWithdraw(w.getText());
            if (!ErrorBus.getInstance().hadError()) w.setText("");
        });

        // Transfer type selector
        JComboBox<String> mode = new JComboBox<>(new String[]{"Internal", "SEPA", "SWIFT"});

        // Internal transfer fields
        JTextField internalToIban = new JTextField();
        JTextField internalAmt = new JTextField();

        // External transfer fields
        JTextField extReceiverIban = new JTextField();
        JTextField extCode = new JTextField();
        JComboBox<String> extCharges = new JComboBox<>(new String[]{"SHA", "OUR"});
        JTextField extAmt = new JTextField();

        JButton trBtn = new JButton("Execute Transfer");
        trBtn.addActionListener(e -> {
            ErrorBus.getInstance().resetLastError();

            String m = (String) mode.getSelectedItem();
            if ("Internal".equalsIgnoreCase(m)) {
                controller.onTransfer(internalToIban.getText(), internalAmt.getText());
                if (!ErrorBus.getInstance().hadError()) {
                    internalToIban.setText("");
                    internalAmt.setText("");
                }
            } else {
                controller.onExternalTransfer(
                        extReceiverIban.getText(),
                        extCode.getText(),
                        (String) extCharges.getSelectedItem(),
                        extAmt.getText(),
                        m
                );
                if (!ErrorBus.getInstance().hadError()) {
                    extReceiverIban.setText("");
                    extCode.setText("");
                    extAmt.setText("");
                }
            }
        });

        // Pay bill
        JTextField rf = new JTextField();
        JButton payBtn = new JButton("Pay RF Bill");
        payBtn.addActionListener(e -> {
            ErrorBus.getInstance().resetLastError();
            controller.onPayRf(rf.getText());
            if (!ErrorBus.getInstance().hadError()) rf.setText("");
        });

        // ---------- layout ----------
        add(new JLabel("Deposit amount:")); add(dep);
        add(new JLabel("")); add(depBtn);

        add(new JLabel("Withdraw amount:")); add(w);
        add(new JLabel("")); add(wBtn);

        add(new JLabel("Transfer type:")); add(mode);

        add(new JLabel("Internal: To IBAN:")); add(internalToIban);
        add(new JLabel("Internal: Amount:")); add(internalAmt);

        add(new JLabel("External: Receiver IBAN:")); add(extReceiverIban);
        add(new JLabel("External: BIC/SWIFT:")); add(extCode);
        add(new JLabel("External: Charges:")); add(extCharges);
        add(new JLabel("External: Amount:")); add(extAmt);

        add(new JLabel("")); add(trBtn);

        add(new JLabel("RF Code:")); add(rf);
        add(new JLabel("")); add(payBtn);

        if (isCompany) {
            JButton load = new JButton("Load Issued Bills (Company)");
            load.addActionListener(e -> controller.onLoadIssuedBills());
            add(new JLabel("")); add(load);
        }

        // ---------- ADMIN ONLY ----------
        if (isAdmin) {
            JTextField date = new JTextField();
            JButton simulateBtn = new JButton("Simulate Until Date");

            simulateBtn.addActionListener(e ->
                    controller.onSimulateUntilDate(date.getText())
            );

            JButton allAccBtn = new JButton("Load ALL Accounts");
            allAccBtn.addActionListener(e ->
                    controller.onAdminLoadAllAccounts()
            );

            add(new JLabel("Admin: simulate until (yyyy-MM-dd):"));
            add(date);
            add(new JLabel("")); add(simulateBtn);
            add(new JLabel("")); add(allAccBtn);
        }
    }
}
