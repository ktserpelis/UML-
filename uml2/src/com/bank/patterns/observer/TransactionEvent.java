package com.bank.patterns.observer;

import java.time.LocalDate;

public class TransactionEvent {
    private final String type; // "Credit" or "Debit"
    private final LocalDate date;
    private final double amount;
    private final String description;
    private final String transactor;
    private final String targetIban; // The IBAN where this statement belongs
    private final String statementSenderIban;
    private final String statementReceiverIban;
    private final double balanceAfter;

    public TransactionEvent(String type, LocalDate date, double amount, String description, 
                          String transactor, String targetIban, String statementSenderIban, 
                          String statementReceiverIban, double balanceAfter) {
        this.type = type;
        this.date = date;
        this.amount = amount;
        this.description = description;
        this.transactor = transactor;
        this.targetIban = targetIban;
        this.statementSenderIban = statementSenderIban;
        this.statementReceiverIban = statementReceiverIban;
        this.balanceAfter = balanceAfter;
    }

    public String getType() { return type; }
    public LocalDate getDate() { return date; }
    public double getAmount() { return amount; }
    public String getDescription() { return description; }
    public String getTransactor() { return transactor; }
    public String getTargetIban() { return targetIban; }
    public String getStatementSenderIban() { return statementSenderIban; }
    public String getStatementReceiverIban() { return statementReceiverIban; }
    public double getBalanceAfter() { return balanceAfter; }
}
