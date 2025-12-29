package com.bank.dao.accounts;

import com.bank.dao.AbstractFsDAO;
import com.bank.model.accounts.Account;
import com.bank.storage.StorableList;

public class FsAccountDAO extends AbstractFsDAO<Account> implements AccountDAO {

    private static final String ACCOUNTS_FILE = "accounts/accounts.csv";

    public FsAccountDAO() {
        super(ACCOUNTS_FILE);
    }

    @Override
    public StorableList<Account> findAll() {
        return loadList();
    }

    @Override
    public void saveAll(StorableList<Account> accounts) {
        saveList(accounts);
    }
}
