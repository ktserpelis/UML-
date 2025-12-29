package com.bank.model.transactions;

import com.bank.storage.Storable;
import com.bank.storage.UnMarshalingException;

import java.time.LocalDate;


public abstract class Transaction implements Storable {
    private static int counter = 1;
    protected String TCode;
    protected String transactor;
    protected String debitReason;
    protected LocalDate date;

    public abstract void execute();

    public Transaction(String transactor, String reason, LocalDate date) {
        this.TCode = generateTransactionId();
        this.transactor = transactor;
        this.debitReason = reason;
    }

    private String generateTransactionId() {
        return "T" + (counter++);
    }


    public void setCounter(int counter) {
        Transaction.counter = counter;
    }

    public String getDebitReason() {
        return debitReason;
    }

    public String getTCode() {
        return TCode;
    }

    public String getTransactor() {
        return transactor;
    }

    //προαιρετικα δεν αποθηκευω transcations
    @Override
    public String marshal() {
        StringBuilder sb = new StringBuilder();
        sb.append("type:").append(this.getClass().getSimpleName()).append(",");
        sb.append("TCode:").append(this.TCode).append(",");
        sb.append("transactor:").append(transactor != null ? transactor : "").append(",");
        sb.append("debitReason:").append(debitReason != null ? debitReason : "").append(",");
        sb.append("date:").append(date != null ? date.toString() : "");

        return sb.toString();
    }



    @Override
    public String toString() {
        return "TCode= " + TCode + ", transactor='" + transactor + ", debitReason='" + debitReason + ", date=" + date;
    }

}