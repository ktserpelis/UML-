package com.bank.ui.gui.commands;

import com.bank.ui.gui.facade.BankFacade;

public class DepositCommand<User,Account,Transaction,Bill> implements Command {
    private final BankFacade<User,Account,Transaction,Bill> facade; private final Account account; private final double amount;
    public DepositCommand(BankFacade<User,Account,Transaction,Bill> facade, Account account, double amount) {
        this.facade = facade; this.account = account; this.amount = amount;
    }
    @Override public void execute() { facade.deposit(account, amount); }
}
