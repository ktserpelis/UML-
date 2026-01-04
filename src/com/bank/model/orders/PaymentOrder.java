package com.bank.model.orders;

import java.time.LocalDate;

public class PaymentOrder extends StandingOrder {
    private String paymentCode;
    private double maxAmount;

    // ✅ add scheduling like TransferOrder
    private int frequencyInMonths;
    private int dayOfMonth;

    public PaymentOrder(String orderId, String title, String description, String customerId,
                        LocalDate startDate, LocalDate endDate, double fee, String chargeAccount,
                        String paymentCode, double maxAmount,
                        int frequencyInMonths, int dayOfMonth) {
        super(orderId, title, description, customerId, startDate, endDate, fee, chargeAccount);
        this.paymentCode = paymentCode;
        this.maxAmount = maxAmount;
        this.frequencyInMonths = frequencyInMonths;
        this.dayOfMonth = dayOfMonth;
    }

    public PaymentOrder() {
        super();
    }

    public String getPaymentCode() { return paymentCode; }
    public double getMaxAmount() { return maxAmount; }

    public String getChargeAccountIban() { return chargeAccount; }

    @Override
    public String marshal() {
        StringBuilder sb = new StringBuilder(super.marshal());
        sb.append(",paymentCode:").append(paymentCode);
        sb.append(",maxAmount:").append(maxAmount);
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
                case "type" -> this.type = value;
                case "orderId" -> this.orderId = value;
                case "title" -> this.title = value;
                case "description" -> this.description = value;
                case "customer" -> this.customer = value;
                case "startDate" -> this.startDate = LocalDate.parse(value);
                case "endDate" -> this.endDate = LocalDate.parse(value);
                case "fee" -> this.fee = Double.parseDouble(value);
                case "chargeAccount" -> this.chargeAccount = value;
                case "paymentCode" -> this.paymentCode = value;
                case "maxAmount" -> this.maxAmount = Double.parseDouble(value);
            }
        }
    }

    @Override
    public String toString() {
        return super.toString()
                + ", maxAmount=" + maxAmount
                + ", paymentCode=" + paymentCode;
    }
}
