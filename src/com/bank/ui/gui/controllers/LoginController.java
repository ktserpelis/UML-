package com.bank.ui.gui.controllers;

import com.bank.ui.gui.commands.LoginCommand;
import com.bank.ui.gui.errors.ErrorBus;
import com.bank.ui.gui.facade.BankFacade;
import com.bank.ui.gui.session.AppSession;

public class LoginController<U, A, T, B> {

    private final BankFacade<U, A, T, B> facade;
    private final AppSession<U, A, T, B> session;
    private final Runnable onSuccessShowDashboard;

    public LoginController(BankFacade<U, A, T, B> facade,
                           AppSession<U, A, T, B> session,
                           Runnable onSuccessShowDashboard) {
        this.facade = facade;
        this.session = session;
        this.onSuccessShowDashboard = onSuccessShowDashboard;
    }

    public void onLogin(String username, String password) {
        new LoginCommand<>(facade, session, username, password, onSuccessShowDashboard).execute();
    }
}
