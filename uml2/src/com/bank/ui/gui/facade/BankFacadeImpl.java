package com.bank.ui.gui.facade;

import com.bank.ui.gui.session.AppSession;

import java.util.List;

public class BankFacadeImpl<U, A, T, B> implements BankFacade<U, A, T, B> {

    private final BackendPort<U, A, T, B> backend;

    public BankFacadeImpl(BackendPort<U, A, T, B> backend) {
        this.backend = backend;
    }

    @Override
    public void onUserSelected(U user, AppSession<U, A, T, B> session) {
        // load data from backend and push into session (Observer triggers UI refresh)
        List<A> accounts = backend.getAccountsForUser(user);
        List<T> transactions = backend.getTransactionsForUser(user);
        List<B> bills = backend.getBillsForUser(user);

        session.clearUserScopedData();
        session.setAccounts(accounts);
        session.setTransactions(transactions);
        session.setBills(bills);

        if (!accounts.isEmpty()) {
            session.setSelectedAccount(accounts.get(0));
        }
    }

    @Override public void deposit(A account, double amount) { backend.deposit(account, amount); }
    @Override public void withdraw(A account, double amount) { backend.withdraw(account, amount); }
    @Override public void transfer(A fromAccount, String toIban, double amount) { backend.transfer(fromAccount, toIban, amount); }
    @Override public void payRfBill(A fromAccount, String rfCode) { backend.payRfBill(fromAccount, rfCode); }

    @Override
    public void loadIssuedBills(U companyUser, AppSession<U, A, T, B> session) {
        List<B> issued = backend.loadIssuedBills(companyUser);
        session.setBills(issued); // updates table via Observer
    }
}
