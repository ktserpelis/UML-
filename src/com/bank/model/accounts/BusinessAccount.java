package com.bank.model.accounts;

import com.bank.model.statements.Statement;
import com.bank.model.users.Company;
import com.bank.storage.UnMarshalingException;

public class BusinessAccount extends Account {
    protected int fee;


    public BusinessAccount(String type,String iban, double balance, double rate, String dateCreated,Company primaryOwner,String ownerVat,int fee) {
        super("BusinessAccount",iban, balance, rate, dateCreated,primaryOwner,ownerVat);
        this.fee = fee;
    }

    public BusinessAccount(){
        super();

    }

    public int getFee() {
        return fee;
    }


    @Override
    public String marshal() {
        StringBuffer sb = new StringBuffer(super.marshal());
        sb.append("fee:").append(this.fee).append(",");
        return sb.toString();
    }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
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
                case "fee":
                    fee = Integer.parseInt(keyValue[1]);
                    break;

            }
        }
    }


    @Override
    public String toString() {
        return super.toString() + " ,Maintenance Fee:" +fee;
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
            throw new IllegalArgumentException("Invalid withdrawal amount");
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
