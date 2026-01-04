package com.bank.model.accounts;

import com.bank.model.statements.Statement;
import com.bank.model.users.Customer;
import com.bank.storage.Storable;

import java.util.ArrayList;
import java.util.List;

public abstract class Account implements Storable {
    protected String type;
    protected String iban;
    protected double balance;
    protected double rate;
    protected String dateCreated;

    //owner and connection with user
    protected String ownerVat;
    protected Customer owner;

    //co-owners and connection with users
    protected List<Customer> coOwners = new ArrayList<>();
    protected List<String> coOwnersVat = new ArrayList<>();

    protected List<Statement> statements = new ArrayList<>();


    public Account(String type, String iban, double balance, double rate, String dateCreated, Customer primaryOwner, String ownerVat) {
        this.type=type;
        this.iban = iban;
        this.balance = balance;
        this.rate = rate;
        this.dateCreated = dateCreated;
        this.ownerVat = primaryOwner.getVatNumber();
    }

    public Account(){
    }


    public String getIban(){
        return iban;
    }
    public double getRate(){
        return rate;
    }

    public double getBalance() {
        return balance;
    }

    protected void setBalance(double balance) {
        this.balance = balance;
    }


    public String getDateCreated() {
        return dateCreated;
    }


    public String getType(){
        return type;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    public Customer getOwner() {
        return owner;
    }

    public void setOwner(Customer owner) {
        this.owner = owner;
    }


    public String getOwnerVat(){
        return ownerVat;
    }

    public void setOwnerVat(String ownerVat) {
        this.ownerVat = ownerVat;
    }

    // Χρήσιμο για DAO:
    public String getPrimaryOwnerVat() {
        return ownerVat != null
                ? ownerVat
                : (owner != null ? owner.getVatNumber() : null);
    }

    public List<Customer> getCoOwners() {
        return coOwners;
    }


    public List<String> getCoOwnersVat() {
        if (!coOwnersVat.isEmpty()) return coOwnersVat;

        List<String> result = new ArrayList<>();
        for (Customer c : coOwners) {
            result.add(c.getVatNumber());
        }
        return result;
    }

    public void setCoOwnersVat(List<String> coOwnersVat) {
        this.coOwnersVat = coOwnersVat;
    }


    public void addCoOwner(Customer coOwner) {
        if (coOwner != null && !coOwners.contains(coOwner)) {
            coOwners.add(coOwner);
        }
    }

    @Override
    public String marshal() {
        StringBuffer sb = new StringBuffer("type:").append(this.type).append(",");
        sb.append("iban:").append(this.iban).append(",");
        sb.append("primaryOwner:").append(this.ownerVat).append(",");
        sb.append("dateCreated:").append(this.dateCreated).append(",");
        sb.append("rate:").append(this.rate).append(",");
        sb.append("balance:").append(this.balance).append(",");
        for (String coVat : getCoOwnersVat()) {
            sb.append("coOwner:").append(coVat).append(",");
        }
        return sb.toString();
    }


    @Override
    public String toString() {
        return "Iban: " + iban + ", Balance: " + balance + ", Rate: " + rate +  ", DateCreated: " + dateCreated + ", Owner: " + ownerVat;
    }

    public abstract void deposit(double amount);

    public abstract void withdraw(double amount) throws Exception ;

    public abstract void addStatement(Statement s) ;

    public boolean isPrimaryOwner(Customer customer) {
        return owner != null && owner.equals(customer);
    }

    public boolean isCoOwner(Customer customer) {
        return coOwners != null && coOwners.contains(customer);
    }


}
