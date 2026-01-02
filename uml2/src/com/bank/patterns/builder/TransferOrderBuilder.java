package com.bank.patterns.builder;

import com.bank.model.orders.TransferOrder;
import com.bank.transfer.TransferProtocol;
import java.time.LocalDate;

public class TransferOrderBuilder {
    // Mandatory fields (could be enforced in constructor, but for Builder often all are optional/settable)
    private String orderId;
    private String customerId;
    private String chargeAccount;
    private String creditAccount;
    private double amount;

    // Optional/Default fields
    private String title = "Transfer Order";
    private String description = "";
    private LocalDate startDate = LocalDate.now();
    private LocalDate endDate = LocalDate.now().plusYears(1);
    private double fee = 0.0;
    private int frequencyInMonths = 1;
    private int dayOfMonth = 1;
    private TransferProtocol protocol = null;

    public TransferOrderBuilder(String orderId, String customerId, String chargeAccount, String creditAccount, double amount) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.chargeAccount = chargeAccount;
        this.creditAccount = creditAccount;
        this.amount = amount;
    }

    public TransferOrderBuilder setTitle(String title) {
        this.title = title;
        return this;
    }

    public TransferOrderBuilder setDescription(String description) {
        this.description = description;
        return this;
    }

    public TransferOrderBuilder setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        return this;
    }

    public TransferOrderBuilder setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        return this;
    }

    public TransferOrderBuilder setFee(double fee) {
        this.fee = fee;
        return this;
    }

    public TransferOrderBuilder setFrequencyInMonths(int frequencyInMonths) {
        this.frequencyInMonths = frequencyInMonths;
        return this;
    }

    public TransferOrderBuilder setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
        return this;
    }

    public TransferOrderBuilder setProtocol(TransferProtocol protocol) {
        this.protocol = protocol;
        return this;
    }

    public TransferOrder build() {
        TransferOrder order = new TransferOrder(
            orderId, title, description, customerId, 
            startDate, endDate, fee, chargeAccount, 
            amount, creditAccount, frequencyInMonths, dayOfMonth
        );
        if (protocol != null) {
            order.setProtocol(protocol);
        }
        return order;
    }
}
