package com.bank.ui.gui.commands;

import com.bank.ui.gui.facade.BankFacade;
import com.bank.ui.gui.session.AppSession;

public class LoadIssuedBillsCommand<User,Account,Transaction,Bill> implements Command {
    private final BankFacade<User,Account,Transaction,Bill> facade; private final User company; private final AppSession<User,Account,Transaction,Bill> session;
    public LoadIssuedBillsCommand(BankFacade<User,Account,Transaction,Bill> facade, User company, AppSession<User,Account,Transaction,Bill> session) {
        this.facade = facade; this.company = company; this.session = session;
    }
    @Override public void execute() { facade.loadIssuedBills(company, session); }
}
