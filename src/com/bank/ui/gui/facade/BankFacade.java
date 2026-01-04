package com.bank.ui.gui.facade;

import com.bank.model.orders.StandingOrder;
import com.bank.ui.gui.session.AppSession;

import java.util.List;

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
    String adminSimulateTimePassing(String dateUntil);

    void logout();

    String createTransferStandingOrder(String title,
                                       String description,
                                       String chargeIban,
                                       String creditIban,
                                       double amount,
                                       String startDate,
                                       String endDate,
                                       int frequencyInMonths,
                                       int dayOfMonth);

    String createPaymentStandingOrder(String title,
                                      String description,
                                      String chargeIban,
                                      String paymentCode,
                                      double maxAmount,
                                      String startDate,
                                      String endDate);

    List<StandingOrder> getMyStandingOrders();

    String companyCreateBill(String customerVat,
                             String amountText,
                             String issueDate,
                             String dueDate,
                             String paymentCode,
                             String billNumber);

}
