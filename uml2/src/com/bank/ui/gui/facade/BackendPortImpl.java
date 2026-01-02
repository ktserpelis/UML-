package com.bank.ui.gui.facade;

import com.bank.model.accounts.Account;
import com.bank.model.bills.Bill;
import com.bank.model.transactions.Transaction;
import com.bank.model.users.Company;
import com.bank.model.users.Customer;
import com.bank.model.users.User;

import java.util.List;

public class BackendPortImpl implements BackendPort<User, Account, Transaction, Bill> {

    // TODO inject your real services here later
    // private final AccountService accountService;
    // private final TransactionService transactionService;
    // private final BillService billService;

    public BackendPortImpl() {
        // later: accept services in constructor
    }

    @Override
    public boolean isCompany(User user) {
        return user instanceof Company
                || (user.getType() != null && user.getType().equalsIgnoreCase("company"));
    }

    @Override
    public List<Account> getAccountsForUser(User user) {
        if (user instanceof Customer c) {
            return c.getAccounts(); // works with your model
        }
        return List.of();
    }

    @Override
    public List<Transaction> getTransactionsForUser(User user) {
        // TODO replace with your real transaction retrieval
        return List.of();
    }

    @Override
    public List<Bill> getBillsForUser(User user) {
        // TODO replace with your real outstanding bills logic
        return List.of();
    }

    @Override
    public void deposit(Account account, double amount) {
        // TODO call your real deposit method (service or account method)
        throw new UnsupportedOperationException("Deposit not wired yet");
    }

    @Override
    public void withdraw(Account account, double amount) {
        // TODO call your real withdraw method
        throw new UnsupportedOperationException("Withdraw not wired yet");
    }

    @Override
    public void transfer(Account fromAccount, String toIban, double amount) {
        // TODO call your real transfer method
        throw new UnsupportedOperationException("Transfer not wired yet");
    }

    @Override
    public void payRfBill(Account fromAccount, String rfCode) {
        // TODO call your real bill payment method
        throw new UnsupportedOperationException("Pay bill not wired yet");
    }

    @Override
    public List<Bill> loadIssuedBills(User companyUser) {
        // TODO call your real issued bills load method
        return List.of();
    }
}
