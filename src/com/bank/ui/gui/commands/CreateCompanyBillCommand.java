package com.bank.ui.gui.commands;

import com.bank.ui.gui.errors.ErrorBus;
import com.bank.ui.gui.facade.BankFacade;

public class CreateCompanyBillCommand<User,Account,Transaction,Bill> {

    private final BankFacade<User,Account,Transaction,Bill> facade;

    private final String customerVat;
    private final String amountText;
    private final String issueDate;
    private final String dueDate;
    private final String paymentCode;
    private final String billNumber;

    public CreateCompanyBillCommand(BankFacade<User,Account,Transaction,Bill> facade,
                                    String customerVat,
                                    String amountText,
                                    String issueDate,
                                    String dueDate,
                                    String paymentCode,
                                    String billNumber) {
        this.facade = facade;
        this.customerVat = customerVat;
        this.amountText = amountText;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.paymentCode = paymentCode;
        this.billNumber = billNumber;
    }

    public String execute() {
        try {
            return facade.companyCreateBill(customerVat, amountText, issueDate, dueDate, paymentCode, billNumber);
        } catch (Exception e) {
            ErrorBus.getInstance().publish("Create Bill failed", e.getMessage());
            return null;
        }
    }
}
