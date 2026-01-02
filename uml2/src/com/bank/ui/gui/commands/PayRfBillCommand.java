package com.bank.ui.gui.commands;

import com.bank.ui.gui.facade.BankFacade;

public class PayRfBillCommand<User,Account,Transaction,Bill> implements Command {
    private final BankFacade<User,Account,Transaction,Bill> facade; private final Account from; private final String rf;
    public PayRfBillCommand(BankFacade<User,Account,Transaction,Bill> facade, Account from, String rf) {
        this.facade = facade; this.from = from; this.rf = rf;
    }
    @Override public void execute() { facade.payRfBill(from, rf); }
}
