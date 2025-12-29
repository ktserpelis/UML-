package com.bank.commands;

import com.bank.managers.TransactionManager;
import com.bank.model.accounts.Account;
import java.time.LocalDate;


public class TransferCommand implements Command {
    private Account sender;
    private Account receiver;
    private double amount;
    private LocalDate date;
    private String transactor;

    public TransferCommand(Account sender, LocalDate date, Account receiver, double amount, String transactor) {
        this.sender = sender;
        this.date = date;
        this.receiver = receiver;
        this.amount = amount;
        this.transactor = transactor;
    }

    @Override
    public void execute() throws Exception {
        TransactionManager.getInstance().executeTransfer(sender, date, receiver, amount, transactor);
    }
}