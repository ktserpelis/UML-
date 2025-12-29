package com.bank.managers;

import com.bank.dao.accounts.AccountDAO;
import com.bank.dao.factory.DAOFactory;
import com.bank.model.accounts.Account;
import com.bank.model.accounts.BusinessAccount;
import com.bank.model.accounts.PersonalAccount;
import com.bank.model.users.Customer;
import com.bank.model.users.User;
import com.bank.storage.*;
import java.time.LocalDate;


public class AccountManager {
    private static AccountManager instance;

    private final AccountDAO accountDAO;
    private final StorableList<Account> accounts;


    private AccountManager() {
        // Παίρνουμε τη DAOFactory για File System (CSV)
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.FS);
        this.accountDAO = factory.getAccountDAO();

        // Φορτώνουμε όλους τους χρήστες από το DAO
        this.accounts = accountDAO.findAll();

        connectAccountsWithCustomers();
    }

    public static AccountManager getInstance() {
        if (instance == null) {
            instance = new AccountManager();
        }
        return instance;
    }

    public void storeBankAccounts() {
        accountDAO.saveAll(accounts);
    }

    private void connectAccountsWithCustomers() {
        UserManager userManager = UserManager.getInstance();


        for (Account acc : accounts) {
            // primary owner
            String ownerVat = acc.getOwnerVat();
            if (ownerVat != null) {
                Customer owner = (Customer) userManager.findCustomerByVat(ownerVat);
                if (owner != null) {
                    acc.setOwner(owner);
                    owner.addAccount(acc);
                }
            }

            // co-owners
            for (String coVat : acc.getCoOwnersVat()) {
                Customer co = (Customer) userManager.findCustomerByVat(coVat);
                if (co != null) {
                    acc.addCoOwner(co);
                    co.addAccount(acc);
                }
            }
        }
    }


    // ========== BUSINESS LOGIC ==========

    public Account authenticate(String iban) {
        try{
            for (Account account : accounts) {
                if (account.getIban().equals(iban)) {
                    return account;
                }
            }
        }catch (Exception e) {
            System.out.println("Iban not found!");
        }
    return null;
    }

    public void addBankAccounts(Account account) {
        for(Account acc : accounts) {
            if(acc.getIban().equals(account.getIban())) {
                return;
            }
        }
        accounts.add(account);
    }


    public StorableList<Account> getBankAccounts() {
        return accounts;
    }

    public Account findAccountByIban(String iban){
        if (iban == null) {
            return null;
        }
        for(Account a : accounts){
            if(iban.equals(a.getIban())){
                return a;
            }
        }
        return null;
    }

    public Account findAccountByVat(String vat){
        if (vat == null) {
            return null;
        }
        for(Account a : accounts){
            if(vat.equals(a.getOwnerVat())){
                return a;
            }
        }
        return null;
    }


    public void calculateInterest(LocalDate date) throws Exception{
        BusinessAccount bankAccount = BankAccountCreator.getInstance().getBankAccount();
        for (Account a : accounts) {
            if (a.equals(bankAccount)) {
                continue;
            }

            double rate = a.getRate();
            double dayInterest = a.getBalance()*(rate/365.0);
            double monthInterest = dayInterest*date.lengthOfMonth();

            if (!(a instanceof BusinessAccount)) {
                TransactionManager.getInstance().executeDeposit(a,date, monthInterest, "Bank", "Monthly Interest");
                TransactionManager.getInstance().executeWithdrawal(bankAccount,date, monthInterest, "Bank", "Interest Paid");

                StatementManager.getInstance().createIndividualStatement("Credit",date,monthInterest,
                        "Monthly Interest", "Bank", bankAccount.getIban(), a.getIban(), a.getBalance());
            } else {
                double fee = ((BusinessAccount) a).getFee();
                try {
                    TransactionManager.getInstance().executeWithdrawal(a,date, fee, "Bank", "Monthly Maintenance Fee");
                    TransactionManager.getInstance().executeDeposit(bankAccount,date, fee, "Bank", "Maintenance Fee Received");

                    StatementManager.getInstance().createCompanyStatement("Debit",date, fee,
                            "Monthly Maintenance Fee", "Bank", a.getIban(), bankAccount.getIban(), a.getBalance());
                } catch (Exception e) {
                    System.out.println("Fee error for account " + a.getIban() + ": " + e.getMessage());
                }
            }
        }
    }

    public void showBankAccounts() {
        StorableList<Account> accounts = AccountManager.getInstance().getBankAccounts();
        System.out.println("-- Personal Accounts --");
        for (Account account : accounts) {
            if (account instanceof PersonalAccount) {
                System.out.println(account);
            }
        }
        System.out.println("-- Business Accounts --");
        for (Account account : accounts) {
            if (account instanceof BusinessAccount) {
                System.out.println(account);
            }
        }
    }

    public void printAccountDetails(String iban){
        Account a = AccountManager.getInstance().authenticate(iban);
        System.out.println("BankAccount Info: " + a.toString());


    }
}
