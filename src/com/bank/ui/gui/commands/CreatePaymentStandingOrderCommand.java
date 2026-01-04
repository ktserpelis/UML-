package com.bank.ui.gui.commands;

import com.bank.ui.gui.errors.ErrorBus;
import com.bank.ui.gui.facade.BankFacade;

public class CreatePaymentStandingOrderCommand<User,Account,Transaction,Bill> {

    private final BankFacade<User,Account,Transaction,Bill> facade;

    private final String title;
    private final String description;
    private final String chargeIban;
    private final String paymentCode;
    private final double maxAmount;
    private final String startDate;
    private final String endDate;

    public CreatePaymentStandingOrderCommand(BankFacade<User,Account,Transaction,Bill> facade,
                                             String title,
                                             String description,
                                             String chargeIban,
                                             String paymentCode,
                                             double maxAmount,
                                             String startDate,
                                             String endDate) {
        this.facade = facade;
        this.title = title;
        this.description = description;
        this.chargeIban = chargeIban;
        this.paymentCode = paymentCode;
        this.maxAmount = maxAmount;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String execute() {
        try {
            return facade.createPaymentStandingOrder(
                    title,
                    description,
                    chargeIban,
                    paymentCode,
                    maxAmount,
                    startDate,
                    endDate
            );
        } catch (Exception e) {
            ErrorBus.getInstance().publish("Create Standing Order failed", e.getMessage());
            return null;
        }
    }
}
