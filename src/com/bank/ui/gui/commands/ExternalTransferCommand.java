package com.bank.ui.gui.commands;

import com.bank.ui.gui.facade.BankFacade;

public class ExternalTransferCommand<User,Account,Transaction,Bill> implements Command {

    private final BankFacade<User,Account,Transaction,Bill> facade;
    private final Account fromAccount;
    private final String receiverIban;
    private final String code;
    private final String charges;
    private final double amount;
    private final String protocol;

    public ExternalTransferCommand(BankFacade<User,Account,Transaction,Bill> facade,
                                   Account fromAccount,
                                   String receiverIban,
                                   String code,
                                   String charges,
                                   double amount,
                                   String protocol) {
        this.facade = facade;
        this.fromAccount = fromAccount;
        this.receiverIban = receiverIban;
        this.code = code;
        this.charges = charges;
        this.amount = amount;
        this.protocol = protocol;
    }

    @Override
    public void execute() {
        facade.externalTransfer(fromAccount, receiverIban, code, charges, amount, protocol);
    }
}
