package com.bank.dao.factory;

import com.bank.dao.accounts.AccountDAO;
import com.bank.dao.bills.DailyBillDAO;
import com.bank.dao.bills.IssuedBillDAO;
import com.bank.dao.bills.PaidBillDAO;
import com.bank.dao.orders.StandingOrderDAO;
import com.bank.dao.statements.StatementDAO;
import com.bank.dao.users.UserDAO;

public abstract class DAOFactory {

    public static final int FS = 1;

    public abstract UserDAO getUserDAO();
    public abstract AccountDAO getAccountDAO();
    public abstract DailyBillDAO getDailyBillDAO();
    public abstract IssuedBillDAO getIssuedBillDAO();
    public abstract PaidBillDAO getPaidBillDAO();
    public abstract StatementDAO getStatementDAO();
    public abstract StandingOrderDAO getStandingOrderDAO();


    public static DAOFactory getDAOFactory(int type) {
        return switch (type) {
            case FS -> new FsDAOFactory();
            default -> throw new IllegalArgumentException("Unknown DAOFactory type: " + type);
        };
    }
}

