package com.bank.model.bills;

import com.bank.storage.Storable;
import com.bank.storage.UnMarshalingException;

import java.time.LocalDate;

public class Bill implements Storable {
    private String type;
    private String paymentCode;
    private String billNumber;
    private String issuer;
    private String customer;
    private double amount;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private boolean paid;


    public Bill(String type, String paymentCode, String billNumber, String issuer,String customer, double amount, LocalDate issueDate, LocalDate dueDate) {
        this.type = type;
        this.paymentCode = paymentCode;
        this.billNumber = billNumber;
        this.issuer = issuer;
        this.customer = customer;
        this.amount = amount;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.paid = false;
    }

    public Bill() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPaymentCode() {
        return paymentCode;
    }

    public void setPaymentCode(String paymentCode) {
        this.paymentCode = paymentCode;
    }

    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getIssuer() {
        return issuer;
    }

    public void setIssuer(String issuer) {
        this.issuer = issuer;
    }

    public String getCustomer() {
        return customer;
    }


    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public boolean getPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    @Override
    public String marshal() {
        StringBuffer sb = new StringBuffer("type:").append(this.type).append(",");
        sb.append("paymentCode:").append(this.paymentCode).append(",");
        sb.append("billNumber:").append(this.billNumber).append(",");
        sb.append("issuer:").append(this.issuer).append(",");
        sb.append("customer:").append(this.customer).append(",");
        sb.append("amount:").append(this.amount).append(",");
        sb.append("issueDate:").append(this.issueDate).append(",");
        sb.append("dueDate:").append(this.dueDate);
        return sb.toString();
    }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        String[] parts = data.split(",");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length != 2){
                continue;
            }
            switch (keyValue[0]) {
                case "type":
                    type = keyValue[1];
                    break;
                case "paymentCode":
                    paymentCode = keyValue[1];
                    break;
                case "billNumber":
                    billNumber = keyValue[1];
                    break;
                case "issuer":
                    issuer = keyValue[1];
                    break;
                case "customer":
                    customer = keyValue[1];
                    break;
                case "amount":
                    amount = Double.parseDouble(keyValue[1]);
                    break;
                case "issueDate":
                    issueDate = LocalDate.parse(keyValue[1]);
                    break;
                case "dueDate":
                    dueDate = LocalDate.parse(keyValue[1]);
                    break;
            }
        }
    }

    @Override
    public String toString() {
        return "Type: " + type + ", Payment Code: " + paymentCode + ", Bill Number: " + billNumber +  ", Issuer: " + issuer
                + ", Customer: " + customer + ", Amount : " + amount + ", Issue Date: " + issueDate + ", Due Date: " + dueDate;
    }
}
