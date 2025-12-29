package com.bank.model.users;

import com.bank.model.accounts.Account;
import java.util.ArrayList;
import java.util.List;

public abstract class Customer extends User{
    private String vatNumber;
    protected List<Account> accounts = new ArrayList<>();



    public Customer(String type, String legalName, String userName, String password, String vatNumber){
        super(type, legalName,userName,password);
        this.vatNumber = vatNumber;
    }

    public Customer(){
        super();
    }



    public String getVatNumber(){
        return vatNumber;
    }


    public List<Account> getAccounts() {
        return accounts;
    }

    public void addAccount(Account account) {
        if (account != null && !accounts.contains(account)) {
            accounts.add(account);
        }
    }

    public void removeAccount(Account account) {
        accounts.remove(account);
    }

    @Override
    public String marshal() {
        StringBuffer sb = new StringBuffer(super.marshal());
        sb.append("vatNumber:").append(this.vatNumber).append(",");
        return sb.toString();
    }

    @Override
    public void unmarshal(String data) {
        String[] parts = data.split(",");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue[0].equals("type")) {
                this.type = keyValue[1];
            } else if (keyValue[0].equals("legalName")) {
                this.legalName = keyValue[1];
            } else if (keyValue[0].equals("userName")) {
                this.userName = keyValue[1];
            } else if (keyValue[0].equals("password")) {
                this.password = keyValue[1];
            }
            else if (keyValue[0].equals("vatNumber")) {
                this.vatNumber = keyValue[1];
            }
        }
    }


    @Override
    public String toString() {
        return super.toString() + ", vat Number: " + vatNumber;
    }
}
