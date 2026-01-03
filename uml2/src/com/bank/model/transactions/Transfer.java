package com.bank.model.transactions;


import com.bank.managers.TransactionManager;
import com.bank.model.accounts.Account;
import com.bank.transfer.TransferProtocol;
import com.bank.patterns.observer.TransactionEvent;
import com.bank.storage.UnMarshalingException;

import java.time.LocalDate;

public class Transfer extends Transaction {
    protected String receiver; // Keep for storage if needed, but we use Account objects for execution
    private Account senderAccount;
    private Account receiverAccount;
    private double amount;
    private TransferProtocol protocol;

    public Transfer(Account sender, Account receiver, double amount, String transactor, String debitReason, LocalDate date, TransferProtocol protocol){
        super(transactor,debitReason,date);
        this.senderAccount = sender;
        this.receiverAccount = receiver;
        this.amount = amount;
        this.receiver = receiver.getIban();
        this.protocol = protocol;
    }
    
    public Transfer(Account sender, Account receiver, double amount, String transactor, String debitReason, LocalDate date){
        this(sender, receiver, amount, transactor, debitReason, date, null);
    }

    public String getReceiver() {
        return receiver;
    }

    @Override
    public void execute() {
        try {
            if (senderAccount.getBalance() < amount) {
                System.out.println("Insufficient balance to pay the bill");
                return;
            }
            
            boolean protocolSuccess = true;
            if (protocol != null) {
                //protocolSuccess = protocol.sendFunds(senderAccount, receiverAccount, amount);
            }

            if (protocolSuccess) {
                senderAccount.withdraw(amount);
                receiverAccount.deposit(amount);
    
//                // Sender Event (Debit)
//                TransactionEvent senderEvent = new TransactionEvent("Debit", date, amount,
//                   //0 "Transfer to: " + receiverAccount.getIban() + (protocol != null ? " via " + protocol.getProtocolName() : ""),
//                    transactor, senderAccount.getIban(),
//                    senderAccount.getIban(), receiverAccount.getIban(), senderAccount.getBalance());
//                TransactionManager.getInstance().notifyObservers(senderEvent);
//
//                // Receiver Event (Credit)
//                TransactionEvent receiverEvent = new TransactionEvent("Credit", date, amount,
//                    //"Transfer from: " + senderAccount.getIban() + (protocol != null ? " via " + protocol.getProtocolName() : ""),
//                    transactor, receiverAccount.getIban(),
//                    senderAccount.getIban(), receiverAccount.getIban(), receiverAccount.getBalance());
//                TransactionManager.getInstance().notifyObservers(receiverEvent);

            } else {
               // System.out.println("Transfer failed by protocol " + (protocol != null ? protocol.getProtocolName() : "Unknown"));
            }

        } catch (Exception e) {
            System.out.println("Error executing transfer: " + e.getMessage());
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
        return "TCode= " + TCode + ", transactor= " + transactor  + ", debitReason='" + debitReason + ", date=" + date;
    }

}