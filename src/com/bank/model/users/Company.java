package com.bank.model.users;

import com.bank.model.accounts.Account;

import java.util.List;

public class Company extends Customer {


    public Company(String type, String legalName, String username, String password, String vatNumber) {
        super(type,legalName,username,password,vatNumber);
    }

    public Company() {
        super();
    }

}

