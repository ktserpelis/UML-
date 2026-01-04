package com.bank.ui.gui;

public interface UserTypeResolver<User> {
    boolean isCompany(User user);
}

