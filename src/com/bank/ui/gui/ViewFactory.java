package com.bank.ui.gui;

import com.bank.model.users.Admin;
import com.bank.ui.gui.controllers.DashboardController;
import com.bank.ui.gui.controllers.LoginController;
import com.bank.ui.gui.facade.BankFacade;
import com.bank.ui.gui.session.AppSession;
import com.bank.ui.gui.views.DashboardPanel;
import com.bank.ui.gui.views.LoginPanel;

public class ViewFactory {

    public <User,Account,Transaction,Bill>
    LoginPanel login(AppSession<User,Account,Transaction,Bill> session,
                     BankFacade<User,Account,Transaction,Bill> facade,
                     Router router,
                     UserTypeResolver<User> typeResolver) {

        Runnable onSuccessShowDashboard = () -> {
            User current = session.getCurrentUser();

            boolean isCompany = typeResolver.isCompany(current);
            boolean isAdmin =
                    current instanceof Admin
                            || (current != null && "admin".equalsIgnoreCase(current.toString()));

            router.setRoot(
                    dashboard(session, facade, router, typeResolver, isCompany, isAdmin)
            );
        };

        var controller = new LoginController<>(facade, session, onSuccessShowDashboard);
        return new LoginPanel(controller);
    }

    public <User,Account,Transaction,Bill>
    DashboardPanel<User,Account,Transaction,Bill> dashboard(
            AppSession<User,Account,Transaction,Bill> session,
            BankFacade<User,Account,Transaction,Bill> facade,
            Router router,
            UserTypeResolver<User> typeResolver,
            boolean isCompany,
            boolean isAdmin) {

        Runnable onLogoutShowLogin = () -> router.setRoot(
                login(session, facade, router, typeResolver)
        );

        var controller = new DashboardController<>(session, facade, isCompany, isAdmin, onLogoutShowLogin);
        return new DashboardPanel<>(session, controller, isCompany, isAdmin);
    }
}
