package com.bank.ui.gui.controllers;

import com.bank.ui.gui.commands.*;
import com.bank.ui.gui.facade.BankFacade;
import com.bank.ui.gui.session.AppSession;

import javax.swing.*;

public class DashboardController<User,Account,Transaction,Bill> {
    private final AppSession<User,Account,Transaction,Bill> session;
    private final BankFacade<User,Account,Transaction,Bill> facade;
    private final boolean isCompany;

    public DashboardController(AppSession<User,Account,Transaction,Bill> session, BankFacade<User,Account,Transaction,Bill> facade, boolean isCompany) {
        this.session = session; this.facade = facade; this.isCompany = isCompany;
    }

    public void onSelectAccount(Account account) {
        if (account != null) session.setSelectedAccount(account);
    }

    public void onDeposit(String amountText) {
        try {
            Account acc = requireAccount();
            double amount = Double.parseDouble(amountText);
            new DepositCommand<>(facade, acc, amount).execute();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    public void onWithdraw(String amountText) {
        try {
            Account acc = requireAccount();
            double amount = Double.parseDouble(amountText);
            new WithdrawCommand<>(facade, acc, amount).execute();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    public void onTransfer(String toIban, String amountText) {
        try {
            Account acc = requireAccount();
            double amount = Double.parseDouble(amountText);
            new TransferCommand<>(facade, acc, toIban, amount).execute();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    public void onPayRf(String rfCode) {
        try {
            Account acc = requireAccount();
            new PayRfBillCommand<>(facade, acc, rfCode).execute();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    public void onLoadIssuedBills() {
        try {
            if (!isCompany) throw new IllegalArgumentException("Company-only action");
            new LoadIssuedBillsCommand<>(facade, session.getCurrentUser(), session).execute();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private Account requireAccount() {
        Account acc = session.getSelectedAccount();
        if (acc == null) throw new IllegalArgumentException("Select an account first");
        return acc;
    }

    private void showError(String msg) {
        JOptionPane.showMessageDialog(null, msg, "Action failed", JOptionPane.ERROR_MESSAGE);
    }
}
