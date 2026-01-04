package com.bank.ui.gui.commands;

import com.bank.ui.gui.facade.BankFacade;

public class CreateTransferStandingOrderCommand<User,Account,Transaction,Bill> {
    private final BankFacade<User,Account,Transaction,Bill> facade;
    private final String title, description, chargeIban, creditIban, startDate, endDate;
    private final double amount;
    private final int frequencyMonths, dayOfMonth;

    public CreateTransferStandingOrderCommand(BankFacade<User,Account,Transaction,Bill> facade,
                                              String title, String description,
                                              String chargeIban, String creditIban,
                                              double amount,
                                              String startDate, String endDate,
                                              int frequencyMonths, int dayOfMonth) {
        this.facade = facade;
        this.title = title;
        this.description = description;
        this.chargeIban = chargeIban;
        this.creditIban = creditIban;
        this.amount = amount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.frequencyMonths = frequencyMonths;
        this.dayOfMonth = dayOfMonth;
    }

    public String execute() {
        return facade.createTransferStandingOrder(title, description, chargeIban, creditIban,
                amount, startDate, endDate, frequencyMonths, dayOfMonth);
    }
}