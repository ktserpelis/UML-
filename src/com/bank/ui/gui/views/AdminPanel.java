package com.bank.ui.gui.views;

import com.bank.ui.gui.controllers.DashboardController;
import com.bank.ui.gui.controllers.TextAreaOutputStream;
import com.bank.ui.gui.errors.ErrorBus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AdminPanel<User, Account, Transaction, Bill> extends JPanel {

    private final JTextArea output = new JTextArea();

    public AdminPanel(DashboardController<User, Account, Transaction, Bill> controller) {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(12, 12, 12, 12));

        JLabel title = new JLabel("Admin");
        title.setFont(title.getFont().deriveFont(Font.BOLD, 16f));
        add(title, BorderLayout.NORTH);

        // Left: buttons + inputs
        JPanel left = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 6, 6, 6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        // Inputs used by some actions
        JTextField vatField = new JTextField();
        JTextField ibanField = new JTextField();
        JTextField billCodeField = new JTextField();
        JTextField dateField = new JTextField(); // yyyy-MM-dd

        AtomicInteger row = new AtomicInteger(0);

        // Helper to add a full-width button row
        java.util.function.BiConsumer<String, Runnable> addButton = (label, action) -> {
            JButton b = new JButton(label);
            b.addActionListener(e -> {
                ErrorBus.getInstance().resetLastError();
                action.run();
            });

            c.gridx = 0;
            c.gridy = row.getAndIncrement();
            c.gridwidth = 2;
            left.add(b, c);
        };

        // Helper: labeled field
        java.util.function.BiConsumer<String, JComponent> addField = (label, field) -> {
            c.gridwidth = 1;

            c.gridx = 0;
            c.gridy = row.get();
            left.add(new JLabel(label), c);

            c.gridx = 1;
            left.add(field, c);

            row.incrementAndGet();
        };

        // Fields
        addField.accept("Customer VAT:", vatField);
        addField.accept("Account IBAN:", ibanField);
        addField.accept("Bill Code:", billCodeField);
        addField.accept("Simulate until (yyyy-MM-dd):", dateField);

        // Buttons
        addButton.accept("1) Show Customers", () ->
                output.setText(controller.adminShowCustomers())
        );

        addButton.accept("2) Show Customer Details", () ->
                output.setText(controller.adminShowCustomerDetails(vatField.getText()))
        );

        addButton.accept("3) Show Bank Accounts", () ->
                output.setText(controller.adminShowBankAccounts())
        );

        addButton.accept("4) Show Bank Account Info", () ->
                output.setText(controller.adminShowBankAccountInfo(ibanField.getText()))
        );

        addButton.accept("5) Show Bank Account Statements", () ->
                output.setText(controller.adminShowBankAccountStatements(ibanField.getText()))
        );

        addButton.accept("6) Show Issued Company Bills", () ->
                output.setText(controller.adminShowIssuedCompanyBills())
        );

        addButton.accept("7) Show Paid Company Bills", () ->
                output.setText(controller.adminShowPaidCompanyBills())
        );

        addButton.accept("8) Load Company Bills (date hardcoded / or field)", () ->
                output.setText(controller.adminLoadCompanyBills())
        );

        addButton.accept("9) List Standing Orders", () ->
                output.setText(controller.adminListStandingOrders())
        );

        addButton.accept("10) Pay Customer’s Bill", () -> {
            controller.adminPayCustomersBill(ibanField.getText(), billCodeField.getText());
            if (!ErrorBus.getInstance().hadError()) {
                output.setText("Bill paid successfully (if all inputs were valid).");
            }
        });

        addButton.accept("11) Simulate Time Passing", () -> {
            output.setText("Running simulation...\n");

            SwingWorker<String, Void> worker = new SwingWorker<>() {
                @Override
                protected String doInBackground() {
                    return controller.adminSimulateTimePassing(dateField.getText());
                }

                @Override
                protected void done() {
                    try {
                        String log = get();
                        if (log == null || log.isBlank()) {
                            output.setText("Simulation finished, but no output was produced.\n");
                        } else {
                            output.setText(log + "\nSimulation finished.\n");
                        }
                    } catch (Exception ex) {
                        output.setText("Simulation failed: " + ex.getMessage());
                    }
                }
            };

            worker.execute();
        });

        // Right: output area
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);

        JScrollPane outScroll = new JScrollPane(output);
        outScroll.setPreferredSize(new Dimension(600, 400));

        // ✅ IMPORTANT: add the UI to the panel
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, outScroll);
        split.setResizeWeight(0.35);
        add(split, BorderLayout.CENTER);
    }
}
