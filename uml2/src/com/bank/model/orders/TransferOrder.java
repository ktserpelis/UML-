package com.bank.model.orders;

import com.bank.transfer.TransferProtocol;
import java.time.LocalDate;

public class TransferOrder extends StandingOrder {
    private double amount;
    private String creditAccount;
    private int frequencyInMonths;
    private int dayOfMonth;
    private TransferProtocol protocol;

    //----------------------------
    private String receiverIban;

    public TransferOrder(String orderId, String title, String description, String customerId,
                         LocalDate startDate, LocalDate endDate, double fee, String chargeAccount,
                         double amount, String creditAccount, int frequencyInMonths, int dayOfMonth) {
        super(orderId, title, description, customerId, startDate, endDate, fee, chargeAccount);
        this.amount = amount;
        this.creditAccount = creditAccount;
        this.frequencyInMonths = frequencyInMonths;
        this.dayOfMonth = dayOfMonth;
    }

    public TransferOrder() {
        super();
    }


    public double getAmount() {
        return amount;
    }
    public String getCreditAccount() {
        return creditAccount;
    }
    public int getFrequencyInMonths() {
        return frequencyInMonths;
    }
    public int getDayOfMonth() {
        return dayOfMonth;
    }
    public String getChargeAccountIban() {
        return chargeAccount;
    }

    public String getReceiverIban() {
        return receiverIban;
    }

    public void setProtocol(TransferProtocol protocol) {
        this.protocol = protocol;
    }

    public TransferProtocol getProtocol() {
        return protocol;
    }

    @Override
    public String marshal() {
        StringBuilder sb = new StringBuilder(super.marshal());
        sb.append(",amount:").append(amount);
        sb.append(",creditAccount:").append(creditAccount);
        sb.append(",frequencyInMonths:").append(frequencyInMonths);
        sb.append(",dayOfMonth:").append(dayOfMonth);
        return sb.toString();
    }

    @Override
    public void unmarshal(String data) {
        String[] parts = data.split(",");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length < 2) continue;

            String key = keyValue[0];
            String value = keyValue[1];

            switch (key) {
                case "type":
                    this.type = value;
                    break;
                case "orderId":
                    this.orderId = value;
                    break;
                case "title":
                    this.title = value;
                    break;
                case "description":
                    this.description = value;
                    break;
                case "customer":
                    this.customer = value;
                    break;
                case "startDate":
                    this.startDate = LocalDate.parse(value);
                    break;
                case "endDate":
                    this.endDate = LocalDate.parse(value);
                    break;
                case "fee":
                    this.fee = Double.parseDouble(value);
                    break;
                case "chargeAccount":
                    this.chargeAccount = value;
                    break;
                case "amount":
                    this.amount = Double.parseDouble(value);
                    break;
                case "creditAccount":
                    this.creditAccount = value;
                    break;
                case "frequencyInMonths":
                    this.frequencyInMonths = Integer.parseInt(value);
                    break;
                case "dayOfMonth":
                    this.dayOfMonth = Integer.parseInt(value);
                    break;
            }
        }
    }


    @Override
    public String toString() {
        return super.toString() + ",amount= " + amount + ", creditAccount='" + creditAccount + ", frequencyInMonths=" + frequencyInMonths +
                ", dayOfMonth= " + dayOfMonth ;
    }
}