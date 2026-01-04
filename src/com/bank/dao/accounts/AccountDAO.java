package com.bank.dao.accounts;

import com.bank.model.accounts.Account;
import com.bank.storage.StorableList;

public interface AccountDAO {
    StorableList<Account> findAll();
    void saveAll(StorableList<Account> accounts);
}
