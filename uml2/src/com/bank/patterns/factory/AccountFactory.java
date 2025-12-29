package com.bank.patterns.factory;

import com.bank.model.accounts.Account;
import com.bank.model.accounts.BusinessAccount;
import com.bank.model.accounts.PersonalAccount;

public class AccountFactory {

    /**
     * Creates an Account object based on the provided type.
     * 
     * @param type The type of account to create (e.g., "Personal", "Business").
     * @return A new instance of the corresponding Account subclass, or null if the type is unknown.
     */
    public Account createAccount(String type) {
        if (type == null) {
            return null;
        }
        
        if (type.equalsIgnoreCase("Personal") || type.equalsIgnoreCase("PersonalAccount")) {
            return new PersonalAccount();
        } else if (type.equalsIgnoreCase("Business") || type.equalsIgnoreCase("BusinessAccount")) {
            return new BusinessAccount();
        }
        
        return null;
    }
}
