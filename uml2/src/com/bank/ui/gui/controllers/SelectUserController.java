package com.bank.ui.gui.controllers;

import com.bank.ui.gui.*;
import com.bank.ui.gui.facade.BankFacade;
import com.bank.ui.gui.session.AppSession;

public class SelectUserController<User,Account,Transaction,Bill> {
    private final AppSession<User,Account,Transaction,Bill> session;
    private final BankFacade<User,Account,Transaction,Bill> facade;
    private final Router router;
    private final ViewFactory factory;
    private final UserTypeResolver<User> typeResolver;

    public SelectUserController(AppSession<User,Account,Transaction,Bill> session,
                                BankFacade<User,Account,Transaction,Bill> facade,
                                Router router,
                                ViewFactory factory,
                                UserTypeResolver<User> typeResolver) {
        this.session = session; this.facade = facade; this.router = router;
        this.factory = factory; this.typeResolver = typeResolver;
    }

    public void onEnter(User user) {
        if (user == null) return;
        session.setCurrentUser(user);
        facade.onUserSelected(user, session);

        boolean isCompany = typeResolver.isCompany(user);
        router.setRoot(factory.dashboard(session, facade, router, isCompany));
    }
}
