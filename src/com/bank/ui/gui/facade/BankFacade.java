package com.bank.ui.gui.facade;

import com.bank.ui.gui.session.AppSession;

public interface BankFacade<U,A,T,B> {
    void onUserSelected(U user, AppSession<U,A,T,B> session);

    void deposit(A account, double amount);
    void withdraw(A account, double amount);

    void transfer(A fromAccount, String toIban, double amount);

    // ✅ NEW
    void externalTransfer(A fromAccount,
                          String receiverIban,
                          String code,     // BIC or SWIFT
                          String charges,  // SHA/OUR
                          double amount,
                          String protocol);// SEPA/SWIFT

    void payRfBill(A fromAccount, String rfCode);
    void loadIssuedBills(U companyUser, AppSession<U,A,T,B> session);
    U login(String username, String password, AppSession<U,A,T,B> session);
    void loadAllAccounts(AppSession<U,A,T,B> session);

    String adminShowCustomers();
    String adminShowCustomerDetails(String vatNumber);
    String adminShowBankAccounts();
    String adminShowBankAccountInfo(String iban);
    String adminShowBankAccountStatements(String iban);

    String adminShowIssuedCompanyBills();
    String adminShowPaidCompanyBills();
    String adminLoadCompanyBills();
    String adminListStandingOrders();

    void adminPayCustomersBill(String customerIban, String billCode);
    void adminSimulateTimePassing(String dateUntil);
}
