package com.bank.ui.gui.facade;

import com.bank.managers.TimeSimulator;
import com.bank.ui.gui.session.AppSession;

import java.util.List;

public class BankFacadeImpl<U, A, T, B> implements BankFacade<U, A, T, B> {

    private final BackendPort<U, A, T, B> backend;

    // We keep a reference so actions can refresh the GUI state.
    private AppSession<U, A, T, B> session;

    public BankFacadeImpl(BackendPort<U, A, T, B> backend) {
        if (backend == null) throw new IllegalArgumentException("backend is null");
        this.backend = backend;
    }

    @Override
    public void onUserSelected(U user, AppSession<U, A, T, B> session) {
        if (session == null) throw new IllegalArgumentException("session is null");
        this.session = session;

        // load data from backend and push into session (Observer triggers UI refresh)
        List<A> accounts = backend.getAccountsForUser(user);
        List<T> transactions = backend.getTransactionsForUser(user);
        List<B> bills = backend.getBillsForUser(user);

        session.setCurrentUser(user);
        session.clearUserScopedData();
        session.setAccounts(accounts);
        session.setTransactions(transactions);
        session.setBills(bills);

        if (accounts != null && !accounts.isEmpty()) {
            session.setSelectedAccount(accounts.get(0));
        } else {
            session.setSelectedAccount(null);
        }
    }

    @Override
    public void deposit(A account, double amount) {
        backend.deposit(account, amount);
        refreshSessionIfPossible();
    }

    @Override
    public void withdraw(A account, double amount) {
        backend.withdraw(account, amount);
        refreshSessionIfPossible();
    }

    @Override
    public void transfer(A fromAccount, String toIban, double amount) {
        backend.transfer(fromAccount, toIban, amount);
        refreshSessionIfPossible();
    }

    @Override
    public void externalTransfer(A fromAccount,
                                 String receiverIban,
                                 String code,
                                 String charges,
                                 double amount,
                                 String protocol) {
        backend.externalTransfer(fromAccount, receiverIban, code, charges, amount, protocol);
        refreshSessionIfPossible();
    }

    @Override
    public void payRfBill(A fromAccount, String rfCode) {
        backend.payRfBill(fromAccount, rfCode);
        refreshSessionIfPossible();
    }

    @Override
    public void loadIssuedBills(U companyUser, AppSession<U, A, T, B> session) {
        if (session == null) throw new IllegalArgumentException("session is null");

        List<B> issued = backend.loadIssuedBills(companyUser);
        session.setBills(issued); // updates table via Observer
    }

    @Override
    public U login(String username, String password, AppSession<U, A, T, B> session) {
        if (session == null) throw new IllegalArgumentException("session is null");

        U user = backend.login(username, password);
        if (user != null) {
            // reuse your existing selection logic to load accounts/tx/bills into session
            onUserSelected(user, session);
        }
        return user;
    }

    @Override
    public void loadAllAccounts(AppSession<U,A,T,B> session) {
        session.setAccounts(backend.getAllAccounts());
    }

    @Override
    public String adminShowCustomers() {
        return backend.adminShowCustomers();
    }

    @Override
    public String adminShowCustomerDetails(String vatNumber) {
        return backend.adminShowCustomerDetails(vatNumber);
    }

    @Override
    public String adminShowBankAccounts() {
        return backend.adminShowBankAccounts();
    }

    @Override
    public String adminShowBankAccountInfo(String iban) {
        return backend.adminShowBankAccountInfo(iban);
    }

    @Override
    public String adminShowBankAccountStatements(String iban) {
        return backend.adminShowBankAccountStatements(iban);
    }

    @Override
    public String adminShowIssuedCompanyBills() {
        return backend.adminShowIssuedCompanyBills();
    }

    @Override
    public String adminShowPaidCompanyBills() {
        return backend.adminShowPaidCompanyBills();
    }

    @Override
    public String adminLoadCompanyBills() {
        String res = backend.adminLoadCompanyBills();
        refreshSessionIfPossible();
        return res;
    }

    @Override
    public String adminListStandingOrders() {
        return backend.adminListStandingOrders();
    }

    @Override
    public void adminPayCustomersBill(String customerIban, String billCode) {
        backend.adminPayCustomersBill(customerIban, billCode);
        refreshSessionIfPossible();
    }

    @Override
    public void adminSimulateTimePassing(String dateUntil) {
        backend.adminSimulateTimePassing(dateUntil);
        refreshSessionIfPossible();
    }


    private void refreshSessionIfPossible() {
        if (session == null) return;
        U user = session.getCurrentUser();
        if (user == null) return;

        // Re-load and push fresh state into the session
        List<A> accounts = backend.getAccountsForUser(user);
        List<T> transactions = backend.getTransactionsForUser(user);
        List<B> bills = backend.getBillsForUser(user);

        session.setAccounts(accounts);
        session.setTransactions(transactions);
        session.setBills(bills);

        // Keep selected account if possible; otherwise fallback to first
        A selected = session.getSelectedAccount();
        if (selected == null && accounts != null && !accounts.isEmpty()) {
            session.setSelectedAccount(accounts.get(0));
        }
    }
}

