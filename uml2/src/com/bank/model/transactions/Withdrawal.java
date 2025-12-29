package com.bank.model.transactions;

import com.bank.managers.AccountManager;
import com.bank.managers.TransactionManager;
import com.bank.model.accounts.Account;
import com.bank.patterns.observer.TransactionEvent;
import com.bank.storage.UnMarshalingException;

import java.time.LocalDate;

public class Withdrawal extends Transaction {
    private Account account;
    private double amount;

    public Withdrawal(Account account, double amount, String transactor, String debitReason, LocalDate date){
        super(transactor,debitReason,date);
        this.account = account;
        this.amount = amount;
    }

    @Override
    public void execute() {
        try {
            if (account.getBalance() < amount) {
                System.out.println("Insufficient balance to pay the bill");
                return;
            }
            account.withdraw(amount);

            TransactionEvent event = new TransactionEvent("Debit", date, amount,
                    debitReason + amount, transactor, account.getIban(),
                    account.getIban(), "None", account.getBalance());

            TransactionManager.getInstance().notifyObservers(event);

            AccountManager.getInstance().storeBankAccounts();
        } catch (Exception e) {
            System.out.println("Error executing withdrawal: " + e.getMessage());
        }
    }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        String[] parts = data.split(",");
        for (String part : parts) {
            String[] keyValue = part.split(":", 2);
            if (keyValue.length != 2) continue;

            switch (keyValue[0]) {
                case "TCode":
                    TCode = keyValue[1];
                    break;
                case "transactor":
                    transactor = keyValue[1];
                    break;
                case "debitReason":
                    debitReason = keyValue[1];
                    break;
                case "date":
                    date = keyValue[1].isEmpty() ? null : LocalDate.parse(keyValue[1]);
                    break;
            }
        }
    }
}