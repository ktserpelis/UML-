package com.bank.ui.gui;

import com.bank.ui.gui.controllers.*;
import com.bank.ui.gui.facade.BankFacade;
import com.bank.ui.gui.session.AppSession;
import com.bank.ui.gui.views.*;

public class ViewFactory {

    public <User,Account,Transaction,Bill> SelectUserPanel<User,Account,Transaction,Bill> selectUser(AppSession<User,Account,Transaction,Bill> session,
                                                        BankFacade<User,Account,Transaction,Bill> facade,
                                                        Router router,
                                                        UserTypeResolver<User> typeResolver) {
        var controller = new SelectUserController<>(session, facade, router, this, typeResolver);
        return new SelectUserPanel<>(session, controller);
    }

    public <User,Account,Transaction,Bill> DashboardPanel<User,Account,Transaction,Bill> dashboard(AppSession<User,Account,Transaction,Bill> session,
                                                      BankFacade<User,Account,Transaction,Bill> facade,
                                                      Router router,
                                                      boolean isCompany) {
        var controller = new DashboardController<>(session, facade, isCompany);
        return new DashboardPanel<>(session, controller, isCompany);
    }
}

