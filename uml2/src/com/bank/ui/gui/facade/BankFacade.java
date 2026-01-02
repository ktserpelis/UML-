package com.bank.ui.gui.facade;

import com.bank.ui.gui.session.AppSession;

public interface BankFacade<User,Account,Transaction,Bill> {
    void onUserSelected(User user, AppSession<User,Account,Transaction,Bill> session);

    void deposit(Account account, double amount);
    void withdraw(Account account, double amount);
    void transfer(Account fromAccount, String toIban, double amount);
    void payRfBill(Account fromAccount, String rfCode);

    void loadIssuedBills(User companyUser, AppSession<User,Account,Transaction,Bill> session);
}
