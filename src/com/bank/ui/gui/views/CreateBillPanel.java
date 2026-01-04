package com.bank.ui.gui.views;

import com.bank.ui.gui.controllers.DashboardController;
import com.bank.ui.gui.errors.ErrorBus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class CreateBillPanel<User,Account,Transaction,Bill> extends JPanel {

    public CreateBillPanel(DashboardController<User,Account,Transaction,Bill> controller) {
        setLayout(new BorderLayout(10,10));
        setBorder(new EmptyBorder(12,12,12,12));

        JLabel title = new JLabel("Create Bill (Company)");
        add(title, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JTextField customerVat = new JTextField();
        JTextField amount = new JTextField();
        JTextField issueDate = new JTextField("2025-12-01");
        JTextField dueDate = new JTextField("2025-12-21");
        JTextField paymentCode = new JTextField(); // optional
        JTextField billNumber = new JTextField();  // optional

        int row = 0;
        addRow(form, c, row++, "Customer VAT:", customerVat);
        addRow(form, c, row++, "Amount:", amount);
        addRow(form, c, row++, "Issue Date (yyyy-MM-dd):", issueDate);
        addRow(form, c, row++, "Due Date (yyyy-MM-dd):", dueDate);
        addRow(form, c, row++, "Payment Code (optional):", paymentCode);
        addRow(form, c, row++, "Bill Number (optional):", billNumber);

        JButton create = new JButton("Create");
        JTextArea output = new JTextArea();
        output.setEditable(false);
        output.setLineWrap(true);
        output.setWrapStyleWord(true);

        create.addActionListener(e -> {
            ErrorBus.getInstance().resetLastError();

            String msg = controller.onCompanyCreateBill(
                    customerVat.getText(),
                    amount.getText(),
                    issueDate.getText(),
                    dueDate.getText(),
                    paymentCode.getText(),
                    billNumber.getText()
            );

            if (!ErrorBus.getInstance().hadError() && msg != null) {
                output.setText(msg);
            }
        });

        JPanel bottom = new JPanel(new BorderLayout(8,8));
        bottom.add(create, BorderLayout.NORTH);
        bottom.add(new JScrollPane(output), BorderLayout.CENTER);

        add(form, BorderLayout.CENTER);
        add(bottom, BorderLayout.SOUTH);
    }

    private void addRow(JPanel panel, GridBagConstraints c, int row, String label, JComponent field) {
        c.gridx = 0; c.gridy = row; c.weightx = 0;
        panel.add(new JLabel(label), c);
        c.gridx = 1; c.weightx = 1;
        panel.add(field, c);
    }
}
