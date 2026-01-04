package com.bank.dao.factory;

import com.bank.dao.accounts.AccountDAO;
import com.bank.dao.accounts.FsAccountDAO;
import com.bank.dao.bills.*;
import com.bank.dao.orders.StandingOrderDAO;
import com.bank.dao.orders.FsStandingOrderDAO;
import com.bank.dao.statements.FsStatementDAO;
import com.bank.dao.statements.StatementDAO;
import com.bank.dao.users.FsUserDAO;
import com.bank.dao.users.UserDAO;

public class FsDAOFactory extends DAOFactory {

    @Override
    public UserDAO getUserDAO() {
        return new FsUserDAO();
    }

    @Override
    public AccountDAO getAccountDAO() {
        return new FsAccountDAO();
    }

    @Override
    public DailyBillDAO getDailyBillDAO() {
        return new FsDailyBillDAO();
    }


    @Override
    public IssuedBillDAO getIssuedBillDAO() {
        return new FsIssuedBillDAO();
    }

    @Override
    public PaidBillDAO getPaidBillDAO() {
        return new FsPaidBillDAO();
    }

    @Override
    public StatementDAO getStatementDAO() {
        return new FsStatementDAO();
    }

    @Override
    public StandingOrderDAO getStandingOrderDAO() {
        return new FsStandingOrderDAO();
    }
}
