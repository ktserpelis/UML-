package com.bank.ui.gui.views;

import com.bank.model.orders.StandingOrder;
import com.bank.ui.gui.controllers.DashboardController;
import com.bank.ui.gui.errors.ErrorBus;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class StandingOrdersPanel<User,Account,Transaction,Bill> extends JPanel {

    private final DefaultListModel<String> listModel = new DefaultListModel<>();
    private final JList<String> list = new JList<>(listModel);

    public StandingOrdersPanel(DashboardController<User,Account,Transaction,Bill> controller) {
        setLayout(new BorderLayout(10,10));
        setBorder(new EmptyBorder(12,12,12,12));

        JLabel title = new JLabel("Standing Orders");
        add(title, BorderLayout.NORTH);

        // left list
        JScrollPane listScroll = new JScrollPane(list);

        // right form
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6,6,6,6);
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        AtomicInteger row = new AtomicInteger();

        JComboBox<String> type = new JComboBox<>(new String[]{"TransferOrder", "PaymentOrder"});
        JTextField titleField = new JTextField();
        JTextField descField = new JTextField();
        JTextField chargeIban = new JTextField();

        // transfer fields
        JTextField creditIban = new JTextField();
        JTextField amount = new JTextField();
        JTextField freqMonths = new JTextField("1");
        JTextField dayOfMonth = new JTextField("1");

        // payment fields
        JTextField paymentCode = new JTextField();
        JTextField maxAmount = new JTextField();

        // common schedule
        JTextField startDate = new JTextField("2025-12-01");
        JTextField endDate = new JTextField("2026-12-31");

        JButton refresh = new JButton("Refresh");
        JButton create = new JButton("Create");

        java.util.function.BiConsumer<String, JComponent> addRow = (label, field) -> {
            c.gridx = 0; c.gridy = row.get(); c.weightx = 0;
            form.add(new JLabel(label), c);
            c.gridx = 1; c.weightx = 1;
            form.add(field, c);
            row.getAndIncrement();
        };

        addRow.accept("Type:", type);
        addRow.accept("Title:", titleField);
        addRow.accept("Description:", descField);
        addRow.accept("Charge IBAN:", chargeIban);

        addRow.accept("Credit IBAN (transfer):", creditIban);
        addRow.accept("Amount (transfer):", amount);
        addRow.accept("Every N months:", freqMonths);
        addRow.accept("Day of month (1-28):", dayOfMonth);

        addRow.accept("Payment Code (payment):", paymentCode);
        addRow.accept("Max Amount (payment):", maxAmount);

        addRow.accept("Start Date (yyyy-MM-dd):", startDate);
        addRow.accept("End Date (yyyy-MM-dd):", endDate);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        btns.add(refresh);
        btns.add(create);

        c.gridx = 0; c.gridy = row.get(); c.gridwidth = 2;
        form.add(btns, c);

        Runnable updateFields = () -> {
            boolean isTransfer = "TransferOrder".equals(type.getSelectedItem());

            creditIban.setEnabled(isTransfer);
            amount.setEnabled(isTransfer);
            freqMonths.setEnabled(isTransfer);
            dayOfMonth.setEnabled(isTransfer);

            paymentCode.setEnabled(!isTransfer);
            maxAmount.setEnabled(!isTransfer);
        };
        type.addActionListener(e -> updateFields.run());
        updateFields.run();

        refresh.addActionListener(e -> loadOrders(controller));

        create.addActionListener(e -> {
            try {
                ErrorBus.getInstance().resetLastError();

                String selected = (String) type.getSelectedItem();
                String msg;

                if ("TransferOrder".equals(selected)) {
                    msg = controller.onCreateTransferStandingOrder(
                            titleField.getText(),
                            descField.getText(),
                            chargeIban.getText(),
                            creditIban.getText(),
                            amount.getText(),
                            startDate.getText(),
                            endDate.getText(),
                            freqMonths.getText(),
                            dayOfMonth.getText()
                    );
                } else {
                    msg = controller.onCreatePaymentStandingOrder(
                            titleField.getText(),
                            descField.getText(),
                            chargeIban.getText(),
                            paymentCode.getText(),
                            maxAmount.getText(),
                            startDate.getText(),
                            endDate.getText()
                    );
                }

                if (!ErrorBus.getInstance().hadError()) {
                    JOptionPane.showMessageDialog(this, msg, "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadOrders(controller);
                }
            } catch (Exception ex) {
                ErrorBus.getInstance().publish("Create Standing Order failed", ex.getMessage());
            }
        });

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listScroll, form);
        split.setResizeWeight(0.45);
        add(split, BorderLayout.CENTER);

        loadOrders(controller);
    }

    private void loadOrders(DashboardController<User,Account,Transaction,Bill> controller) {
        listModel.clear();
        List<StandingOrder> orders = controller.getMyStandingOrders();
        if (orders == null || orders.isEmpty()) {
            listModel.addElement("No standing orders.");
            return;
        }
        for (StandingOrder o : orders) {
            listModel.addElement(o.marshal());
        }
    }
}
