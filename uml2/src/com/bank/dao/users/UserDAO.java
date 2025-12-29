package com.bank.dao.users;

import com.bank.model.users.User;
import com.bank.storage.StorableList;

public interface UserDAO {
    StorableList<User> findAll();
    void saveAll(StorableList<User> users);
}
