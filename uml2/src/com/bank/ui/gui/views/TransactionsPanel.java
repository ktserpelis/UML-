package com.bank.ui.gui.views;

import com.bank.ui.gui.session.AppSession;
import com.bank.ui.gui.views.models.TransactionsTableModel;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TransactionsPanel<User,Account,Transaction,Bill> extends JPanel implements PropertyChangeListener {
    private final AppSession<User,Account,Transaction,Bill> session;
    private final TransactionsTableModel<Transaction> model = new TransactionsTableModel<>();

    public TransactionsPanel(AppSession<User,Account,Transaction,Bill> session) {
        this.session = session;
        setLayout(new BorderLayout());
        add(new JScrollPane(new JTable(model)), BorderLayout.CENTER);

        session.addListener(this);
        model.setRows(session.getTransactions());
    }

    @Override public void propertyChange(PropertyChangeEvent evt) {
        if (AppSession.EVT_TRANSACTIONS.equals(evt.getPropertyName())) {
            model.setRows(session.getTransactions());
        }
    }
}
