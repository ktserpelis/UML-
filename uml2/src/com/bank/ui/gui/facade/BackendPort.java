package com.bank.ui.gui.facade;

import java.util.List;

public interface BackendPort<U, A, T, B> {
    boolean isCompany(U user);

    List<A> getAccountsForUser(U user);
    List<T> getTransactionsForUser(U user);
    List<B> getBillsForUser(U user);       // outstanding (individual) or visible bills

    void deposit(A account, double amount);
    void withdraw(A account, double amount);
    void transfer(A fromAccount, String toIban, double amount);
    void payRfBill(A fromAccount, String rfCode);

    List<B> loadIssuedBills(U companyUser); // company-only list
}
