package com.bank.managers;

import com.bank.model.accounts.BusinessAccount;
import com.bank.model.users.Company;

public class BankAccountCreator {
    private static BankAccountCreator instance;
    private BusinessAccount bankAccount;
    private Company bank;

    public static BankAccountCreator getInstance() {
        if (instance == null) {
            instance = new BankAccountCreator();
        }
        return instance;
    }

    private BankAccountCreator() {
        this.bank = new Company("Company","Bank Of Tuc","bankoftuc",
                "123456","090800123");
        UserManager.getInstance().addUser(bank);

        this.bankAccount = new BusinessAccount("BusinessAccount","GR200000000000000000",10000000.0,0.0,"2005-03-10",
                bank,bank.getVatNumber(),0);
        AccountManager.getInstance().addBankAccounts(bankAccount);
    }

    public BusinessAccount getBankAccount() {
        return bankAccount;
    }
}
