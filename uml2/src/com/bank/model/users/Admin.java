package com.bank.model.users;

public class Admin extends User {

    public Admin(String type, String legalName, String userName, String password) {
        super(type, legalName, userName, password);
    }


    public Admin() {
        super();
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
        }
    }
}
