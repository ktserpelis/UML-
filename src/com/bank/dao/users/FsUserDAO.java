package com.bank.dao.users;

import com.bank.dao.AbstractFsDAO;
import com.bank.model.users.User;
import com.bank.storage.StorableList;

public class FsUserDAO extends AbstractFsDAO<User> implements UserDAO {

    private static final String USERS_FILE = "users/users.csv";

    public FsUserDAO() {
        super(USERS_FILE);
    }

    @Override
    public StorableList<User> findAll() {
        return loadList();
    }

    @Override
    public void saveAll(StorableList<User> users) {
        saveList(users);
    }
}
