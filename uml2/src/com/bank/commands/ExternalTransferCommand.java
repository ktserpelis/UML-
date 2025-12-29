package com.bank.commands;

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
        // Ελέγχουμε αν ο sender έχει το ποσό πριν "χτυπήσουμε" το API
        if (sender.getBalance() < amount) {
            throw new Exception("Insufficient balance for external transfer");
        }

        // Η "Γέφυρα" (Bridge) κάνει το HTTP Call
        boolean success = bridge.execute(amount, receiverIban, code, charges);

        if (success) {
            sender.withdraw(amount);
            // Ενημέρωση Statement για την εξωτερική μεταφορά
            com.bank.managers.StatementManager.getInstance().createIndividualStatement(
                    "External Transfer", date, amount, "Transfer to " + receiverIban + " (" + code + ")",
                    "SELF", sender.getIban(), receiverIban, sender.getBalance()
            );
        } else {
            throw new Exception("The external bank rejected the transaction.");
        }
    }
}