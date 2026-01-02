package com.bank.ui.gui;

import com.bank.dao.factory.DAOFactory;
import com.bank.dao.users.UserDAO;
import com.bank.model.accounts.Account;
import com.bank.model.bills.Bill;
import com.bank.model.transactions.Transaction;
import com.bank.model.users.User;
import com.bank.storage.StorableList;
import com.bank.ui.gui.facade.BackendPort;
import com.bank.ui.gui.facade.BackendPortImpl;
import com.bank.ui.gui.facade.BankFacade;
import com.bank.ui.gui.facade.BankFacadeImpl;
import com.bank.ui.gui.session.AppSession;

import javax.swing.*;
import java.awt.*;

public class SwingMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {

            JFrame frame = new JFrame("Bank App - Swing GUI");
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
            frame.setSize(1100, 700);
            frame.setLayout(new BorderLayout());

            AppSession<User, Account, Transaction, Bill> session = new AppSession<>();

            // --- Load users from CSV (DAO) ---
            DAOFactory daoFactory = DAOFactory.getDAOFactory(DAOFactory.FS);
            UserDAO userDAO = daoFactory.getUserDAO();
            StorableList<User> usersFromDao = userDAO.findAll();

            // ✅ Works because StorableList extends ArrayList => is a List
            session.setUsers(usersFromDao);

            // --- Backend + Facade ---
            BackendPort<User, Account, Transaction, Bill> backend = new BackendPortImpl();
            BankFacade<User, Account, Transaction, Bill> facade = new BankFacadeImpl<>(backend);

            // --- Navigation + UI Factory ---
            Router router = new Router(frame);
            ViewFactory viewFactory = new ViewFactory();

            UserTypeResolver<User> typeResolver = backend::isCompany;

            router.setRoot(viewFactory.selectUser(session, facade, router, typeResolver));
            frame.setVisible(true);
        });
    }
}
