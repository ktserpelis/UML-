package com.bank.model.orders;

import java.time.LocalDate;

public class PaymentOrder extends StandingOrder{
    private String paymentCode;
    private double maxAmount;

    public PaymentOrder(String orderId, String title, String description, String customerId,
                        LocalDate startDate, LocalDate endDate, double fee, String chargeAccount,
                        String paymentCode, double maxAmount) {
        super(orderId, title, description, customerId, startDate, endDate, fee, chargeAccount);
        this.paymentCode = paymentCode;
        this.maxAmount = maxAmount;
    }

    public PaymentOrder() {
        super();
    }


    public String getPaymentCode() {
        return paymentCode;
    }
    public double getMaxAmount() {
        return maxAmount;
    }

    @Override
    public String marshal() {
        StringBuilder sb = new StringBuilder(super.marshal());
        sb.append(",paymentCode:").append(paymentCode);
        sb.append(",maxAmount:").append(maxAmount);
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
                case "paymentCode":
                    this.paymentCode = value;
                    break;
                case "maxAmount":
                    this.maxAmount = Double.parseDouble(value);
                    break;
            }
        }
    }

    @Override
    public String toString() {
        return super.toString() + "maxAmount= " + maxAmount + ", paymentCode= " + paymentCode ;
    }

    public String getChargeAccountIban() {
        return chargeAccount;
    }
}
