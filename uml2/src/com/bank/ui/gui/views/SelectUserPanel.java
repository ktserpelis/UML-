package com.bank.ui.gui.views;

import com.bank.ui.gui.controllers.SelectUserController;
import com.bank.ui.gui.session.AppSession;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class SelectUserPanel<User,Account,Transaction,Bill> extends JPanel implements PropertyChangeListener {
    private final AppSession<User,Account,Transaction,Bill> session;
    private final SelectUserController<User,Account,Transaction,Bill> controller;

    private final DefaultComboBoxModel<User> model = new DefaultComboBoxModel<>();
    private final JComboBox<User> combo = new JComboBox<>(model);

    public SelectUserPanel(AppSession<User,Account,Transaction,Bill> session, SelectUserController<User,Account,Transaction,Bill> controller) {
        this.session = session;
        this.controller = controller;

        setLayout(new BorderLayout(10, 10));
        JPanel center = new JPanel(new GridLayout(0,1,8,8));

        center.add(new JLabel("Select user (no auth; users loaded from CSV):"));
        center.add(combo);

        JButton enter = new JButton("Enter");
        enter.addActionListener(e -> controller.onEnter((User) combo.getSelectedItem()));

        add(center, BorderLayout.CENTER);
        add(enter, BorderLayout.SOUTH);

        session.addListener(this);
        reloadUsers();
    }

    private void reloadUsers() {
        model.removeAllElements();
        for (User u : session.getUsers()) model.addElement(u);
    }

    @Override public void propertyChange(PropertyChangeEvent evt) {
        if (AppSession.EVT_USERS.equals(evt.getPropertyName())) reloadUsers();
    }
}

