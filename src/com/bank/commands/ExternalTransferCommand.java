package com.bank.commands;

import com.bank.managers.TransactionManager;
import com.bank.transfer.ExternalTransfer;
import com.bank.model.accounts.Account;
import java.time.LocalDate;

public class ExternalTransferCommand implements Command {
    private ExternalTransfer bridge;
    private Account sender;
    private double amount;
    private String receiverIban;
    private String code; // BIC or SWIFT
    private String charges;
    private LocalDate date;

    public ExternalTransferCommand(ExternalTransfer bridge, Account sender, double amount, String receiverIban, String code, String charges, LocalDate date) {
        this.bridge = bridge;
        this.sender = sender;
        this.amount = amount;
        this.receiverIban = receiverIban;
        this.code = code;
        this.charges = charges;
        this.date = date;
    }

    @Override
    public void execute() throws Exception {
        TransactionManager.getInstance().executeExternalTransfer(sender,date,receiverIban,code,amount,charges,bridge,"SELF");
    }
}