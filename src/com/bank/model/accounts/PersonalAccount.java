package com.bank.model.accounts;

import com.bank.model.statements.Statement;
import com.bank.model.users.Individual;
import com.bank.storage.UnMarshalingException;
import java.util.ArrayList;
import java.util.List;


public class PersonalAccount extends Account {
    protected List<String> coOwnerVat;
    protected List<Individual> coOwners;



    public PersonalAccount(String type,String iban, double balance, double rate,String dateCreated,
                           Individual primaryOwner, String ownerVat,List<Individual> coOwners) {

        super("PersonalAccount",iban, balance, rate, dateCreated,primaryOwner,ownerVat);
        this.coOwnerVat = new ArrayList<>();
        this.coOwners = new ArrayList<>();

        if (coOwners != null) {
            for (Individual i : coOwners) {
                if (!i.getVatNumber().equals(ownerVat)) {
                    this.coOwners.add(i);
                    this.coOwnerVat.add(i.getVatNumber());
                }
            }
        }

    }

    public PersonalAccount(){
        super();
        this.coOwnerVat = new ArrayList<>();
        this.coOwners = new ArrayList<>();
    }

    public boolean whichOwner(String vat) {
        return ownerVat.equals(vat) || coOwnerVat.contains(vat);
    }


    @Override
    public String marshal() {
        StringBuffer sb = new StringBuffer(super.marshal());
        for (String vat : coOwnerVat) {
            sb.append("coOwner:").append(vat).append(",");
        }
        return sb.toString();
    }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        this.coOwnerVat = new ArrayList<>();
        this.coOwners = new ArrayList<>();

        String[] parts = data.split(",");
        for (String part : parts) {
            String[] keyValue = part.split(":");
            if (keyValue.length != 2){
                continue;
            }
            switch (keyValue[0]) {
                case "type":
                    type = keyValue[1];
                    break;
                case "iban":
                    iban = keyValue[1];
                    break;
                case "primaryOwner":
                    ownerVat = keyValue[1];
                    break;
                case "dateCreated":
                    dateCreated = keyValue[1];
                    break;
                case "rate":
                    rate = Double.parseDouble(keyValue[1]);
                    break;
                case "balance":
                    balance = Double.parseDouble(keyValue[1]);
                    break;
                case "coOwner":
                    coOwnersVat.add(keyValue[1]);
                    break;
            }
        }
    }

    @Override
    public String toString() {
        List<String> coOwners = new ArrayList<>();
        for (String vat : coOwnerVat) {
            if (!vat.equals(ownerVat)) {
                coOwners.add(vat);
            }
        }
        return super.toString() + ", Co-owners: "+ coOwners;
    }

    @Override
    public void deposit(double amount) {
        try{
            if(amount>0){
                setBalance(getBalance() + amount);
            }
        }catch(IllegalArgumentException e){
            System.out.println("Invalid deposit amount");
        }
    }

    @Override
    public void withdraw(double amount) throws Exception {
        if (amount <= 0) {
            throw new IllegalArgumentException("Invalid withdraw amount");
        }
        if (amount > getBalance()) {
            throw new Exception("Insufficient funds");
        }
        setBalance(getBalance() - amount);
    }

    public void addStatement(Statement s) {
        statements.add(0, s);
    }
}
