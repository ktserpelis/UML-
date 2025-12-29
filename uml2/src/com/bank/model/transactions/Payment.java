package com.bank.model.transactions;

import com.bank.managers.TransactionManager;
import com.bank.model.accounts.BusinessAccount;
import com.bank.model.accounts.PersonalAccount;
import com.bank.model.bills.Bill;
import com.bank.patterns.observer.TransactionEvent;
import com.bank.storage.UnMarshalingException;

import java.time.LocalDate;

public class Payment extends Transaction {
    protected String receiver;
    private PersonalAccount payer;
    private BusinessAccount receiverAccount;
    private Bill bill;

    public Payment(PersonalAccount payer, BusinessAccount receiverAccount, Bill bill, String transactor, String debitReason, LocalDate date){
        super(transactor,debitReason,date);
        this.payer = payer;
        this.receiverAccount = receiverAccount;
        this.bill = bill;
        this.receiver = receiverAccount.getIban();
    }

    public String getReceiver() {
        return receiver;
    }

    @Override
    public void execute() {
        try {
            if (payer.getBalance() < bill.getAmount()) {
                System.out.println("Insufficient balance to pay the bill");
                return;
            }

            payer.withdraw(bill.getAmount());
            receiverAccount.deposit(bill.getAmount());

            // Payer Event (Debit)
            TransactionEvent payerEvent = new TransactionEvent("Debit", date, bill.getAmount(), 
                "Bill Payment for " + receiverAccount.getIban(), transactor, payer.getIban(), 
                payer.getIban(), receiverAccount.getIban(), payer.getBalance());
            TransactionManager.getInstance().notifyObservers(payerEvent);

            // Receiver Event (Credit)
            TransactionEvent receiverEvent = new TransactionEvent("Credit", date, bill.getAmount(), 
                "Bill: " + bill.getBillNumber() + "From: " + payer.getIban(), transactor, receiverAccount.getIban(), 
                payer.getIban(), receiverAccount.getIban(), receiverAccount.getBalance());
            TransactionManager.getInstance().notifyObservers(receiverEvent);

        } catch (Exception e) {
            System.out.println("Error executing payment: " + e.getMessage());
        }
    }

    @Override
    public String marshal() {
        StringBuffer sb = new StringBuffer(super.marshal());
        sb.append("Vat Number:").append(this.receiver).append(",");
        return sb.toString();
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


    @Override
    public String toString() {
        return "TCode= " + TCode +  ", transactor= " + transactor  + ", debitReason= " + debitReason + ", date= " + date;
    }
}