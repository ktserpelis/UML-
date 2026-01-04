package com.bank.ui.gui.commands;

import com.bank.ui.gui.facade.BankFacade;
import com.bank.ui.gui.session.AppSession;

public class AdminLoadAllAccountsCommand<User,Account,Transaction,Bill> implements Command {

    private final BankFacade<User,Account,Transaction,Bill> facade;
    private final AppSession<User,Account,Transaction,Bill> session;

    public AdminLoadAllAccountsCommand(
            BankFacade<User,Account,Transaction,Bill> facade,
            AppSession<User,Account,Transaction,Bill> session) {
        this.facade = facade;
        this.session = session;
    }

    @Override
    public void execute() {
        facade.loadAllAccounts(session);
    }
}
