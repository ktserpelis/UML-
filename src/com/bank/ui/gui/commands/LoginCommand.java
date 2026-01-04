package com.bank.ui.gui.commands;

import com.bank.ui.gui.errors.ErrorBus;
import com.bank.ui.gui.facade.BankFacade;
import com.bank.ui.gui.session.AppSession;

public class LoginCommand<User,Account,Transaction,Bill> implements Command {

    private final BankFacade<User,Account,Transaction,Bill> facade;
    private final AppSession<User,Account,Transaction,Bill> session;
    private final String username;
    private final String password;
    private final Runnable onSuccessShowDashboard;

    public LoginCommand(BankFacade<User,Account,Transaction,Bill> facade,
                        AppSession<User,Account,Transaction,Bill> session,
                        String username,
                        String password,
                        Runnable onSuccessShowDashboard) {
        this.facade = facade;
        this.session = session;
        this.username = username;
        this.password = password;
        this.onSuccessShowDashboard = onSuccessShowDashboard;
    }

    @Override
    public void execute() {
        try {
            User user = facade.login(username, password, session);

            if (user == null) {
                ErrorBus.getInstance().publish("Login failed", "Invalid credentials!");
                return;
            }

            // success: just navigate
            onSuccessShowDashboard.run();

        } catch (IllegalArgumentException e) {
            ErrorBus.getInstance().publish("Invalid input", e.getMessage());
        } catch (Exception e) {
            ErrorBus.getInstance().publish("Login error", e.getMessage());
        }
    }
}
