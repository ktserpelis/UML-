package com.bank.storage;

import java.util.ArrayList;

public class StorableList<T extends Storable> extends ArrayList<T> implements Storable {

    @Override
    public String marshal() {
        StringBuffer sb = new StringBuffer();
        for (T item : this) {
            sb.append(item.marshal()).append("\n");
        }
        return sb.toString();
    }

    @Override
    public void unmarshal(String data) throws UnMarshalingException {
        try {
            String[] lines = data.split("\n");
            for (String line : lines) {
                try {
                    String[] parts = line.split(",");
                    String type = parts[0].split(":")[1].trim();
                    String className;
                    switch (type) {
                        case "Admin":
                            className = "com.bank.model.users.Admin";
                            break;
                        case "Individual":
                            className = "com.bank.model.users.Individual";
                            break;
                        case "Company":
                            className = "com.bank.model.users.Company";
                            break;
                        case "PersonalAccount":
                            className = "com.bank.model.accounts.PersonalAccount";
                            break;
                        case "BusinessAccount":
                            className = "com.bank.model.accounts.BusinessAccount";
                            break;
                        case "Bill":
                            className = "com.bank.model.bills.Bill";
                            break;
                        case "Statement":
                            className = "com.bank.model.statements.Statement";
                            break;
                        case "TransferOrder":
                            className = "com.bank.model.orders.TransferOrder";
                            break;
                        case "PaymentOrder":
                            className = "com.bank.model.orders.PaymentOrder";
                            break;
                        default:
                            System.out.println("Unknown user type: " + type);
                            continue;
                    }
                    Class<?> typeClass = Class.forName(className);
                    if (typeClass != null) {
                        @SuppressWarnings("unchecked")
                        T item = (T) typeClass.getDeclaredConstructor().newInstance();
                        item.unmarshal(line);
                        add(item);
                    }
                } catch (UnMarshalingException e) {
                    System.out.println("Error unmarshalling item: " + e.getMessage());
                } catch (Exception e) {
                    System.out.println("Error creating instance of class: " + e.getMessage());
                }

        }
        }catch (Exception e) {
            throw new UnMarshalingException(e.getMessage());
        }
    }
}
