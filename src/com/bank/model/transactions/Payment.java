package com.bank.model.transactions;

import com.bank.storage.UnMarshalingException;

import java.time.LocalDate;

public class Payment extends Transaction {
    protected String receiver;

    public Payment(String transactor, String debitReason, String  receiver, LocalDate date){
        super(transactor,debitReason,date);
        this.receiver= receiver;
    }

    public String getReceiver() {
        return receiver;
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
