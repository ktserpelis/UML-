package com.bank.ui.gui.commands;

import com.bank.ui.gui.facade.BankFacade;

public class TransferCommand<User,Account,Transaction,Bill> implements Command {
    private final BankFacade<User,Account,Transaction,Bill> facade; private final Account from; private final String toIban; private final double amount;
    public TransferCommand(BankFacade<User,Account,Transaction,Bill> facade, Account from, String toIban, double amount) {
        this.facade = facade; this.from = from; this.toIban = toIban; this.amount = amount;
    }
    @Override public void execute() { facade.transfer(from, toIban, amount); }
}

