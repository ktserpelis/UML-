package com.bank.model.orders;

import com.bank.storage.Storable;

import java.time.LocalDate;

public abstract class StandingOrder implements Storable {
    protected String type;
    protected String orderId;
    protected String title;
    protected String description;
    protected String customer;
    protected LocalDate startDate;
    protected LocalDate endDate;
    protected double fee;
    protected String chargeAccount;

    //-------------------------------
    protected boolean active = true;
    protected int failedAttempts;       // συνεχόμενες αποτυχίες
    protected int maxAttempts = 3;      // μετά από 3 αποτυχίες → απενεργοποίηση
    protected Integer frequencyMonths;  // κάθε πόσους μήνες


    public StandingOrder(String orderId, String title, String description, String customer,
                         LocalDate startDate, LocalDate endDate, double fee, String chargeAccount) {
        this.orderId = orderId;
        this.title = title;
        this.description = description;
        this.customer = customer;
        this.startDate = startDate;
        this.endDate = endDate;
        this.fee = fee;
        this.chargeAccount = chargeAccount;
    }

    public StandingOrder() {

    }


    public String getOrderId() {
        return orderId;
    }
    public String getTitle() {
        return title;
    }
    public String getDescription() {
        return description;
    }
    public String getCustomer() {
        return customer;
    }
    public LocalDate getStartDate() {
        return startDate;
    }
    public LocalDate getEndDate() {
        return endDate;
    }
    public double getFee() {
        return fee;
    }
    public String getChargeAccount() {
        return chargeAccount;
    }
    public int getFailedAttempts() {
        return failedAttempts;
    }
    public int getMaxAttempts() {
        return maxAttempts;
    }
    public void setFailedAttempts(int i) {
        this.failedAttempts = i;
    }
    public void setActive(boolean b) {
        this.active = b;
    }

    public boolean isActive() {
        return active;
    }

    public Integer getFrequencyMonths() {
        return frequencyMonths;
    }


    @Override
    public String marshal() {
        StringBuilder sb = new StringBuilder();
        sb.append("type:").append(this.type).append(",");
        sb.append("orderId:").append(orderId).append(",");
        sb.append("title:").append(title).append(",");
        sb.append("description:").append(description).append(",");
        sb.append("customer:").append(customer).append(",");
        sb.append("startDate:").append(startDate).append(",");
        sb.append("endDate:").append(endDate).append(",");
        sb.append("fee:").append(fee).append(",");
        sb.append("chargeAccount:").append(chargeAccount);
        return sb.toString();
    }


    @Override
    public String toString() {
        return super.toString() + ", type='" + type + "chargeAccount='" + chargeAccount + ", orderId='" + orderId + '\'' +
                ", title='" + title + ", description='" + description + ", customer='" + customer + ", startDate=" + startDate +
                ", endDate=" + endDate + ", fee=" + fee;
    }
}

