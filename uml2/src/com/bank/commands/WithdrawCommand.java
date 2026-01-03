package com.bank.commands;

import com.bank.managers.TransactionManager;
import com.bank.model.accounts.Account;
import java.time.LocalDate;


public class WithdrawCommand implements Command {
    private Account account;
    private double amount;
    private LocalDate date;
    private String transactor;
    private String description;

    public WithdrawCommand(Account account, LocalDate date, double amount, String transactor, String description) {
        this.account = account;
        this.date = date;
        this.amount = amount;
        this.transactor = transactor;
        this.description = description;
    }

    @Override
    public void execute() throws Exception {
        TransactionManager.getInstance().executeWithdrawal(account, date, amount, transactor, description);
    }
}