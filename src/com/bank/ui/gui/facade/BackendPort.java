package com.bank.ui.gui.facade;

import com.bank.model.orders.StandingOrder;

import java.util.List;

public interface BackendPort<U, A, T, B> {
    boolean isCompany(U user);

    List<A> getAccountsForUser(U user);
    List<T> getTransactionsForUser(U user);
    List<B> getBillsForUser(U user);       // outstanding (individual) or visible bills
    List<A> getAllAccounts();

    void simulateUntil(String date);
    void deposit(A account, double amount);
    void withdraw(A account, double amount);
    void transfer(A fromAccount, String toIban, double amount);
    void payRfBill(A fromAccount, String rfCode);

    void externalTransfer(A fromAccount,
                          String receiverIban,
                          String bankCodeOrBicOrSwift,
                          String charges,
                          double amount,
                          String protocol); // "SEPA" ή "SWIFT"


    List<B> loadIssuedBills(U companyUser); // company-only list
    U login(String username, String password);

    String adminShowCustomers();
    String adminShowCustomerDetails(String vatNumber);
    String adminShowBankAccounts();
    String adminShowBankAccountInfo(String iban);
    String adminShowBankAccountStatements(String iban);

    String adminShowIssuedCompanyBills();
    String adminShowPaidCompanyBills();
    String adminLoadCompanyBills();            // loads daily bills for fixed date like CLI
    String adminListStandingOrders();

    void adminPayCustomersBill(String customerIban, String billCode);
    String adminSimulateTimePassing(String dateUntil); // "YYYY-MM-DD"

    // Create orders
    String createTransferStandingOrder(String customerVat,
                                       String title,
                                       String description,
                                       String chargeIban,
                                       String creditIban,
                                       double amount,
                                       String startDate,
                                       String endDate,
                                       int frequencyInMonths,
                                       int dayOfMonth);

    String createPaymentStandingOrder(String customerVat,
                                      String title,
                                      String description,
                                      String chargeIban,
                                      String paymentCode,
                                      double maxAmount,
                                      String startDate,
                                      String endDate);

    List<StandingOrder> getStandingOrdersForCustomer(String customerVat);

    String companyCreateBill(String issuerVat,
                             String customerVat,
                             double amount,
                             String issueDate,
                             String dueDate,
                             String paymentCode,
                             String billNumber);

}
