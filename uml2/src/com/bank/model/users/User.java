package com.bank.model.users;

import com.bank.storage.Storable;

public abstract class User implements Storable{
    protected String type;
    protected String legalName;
    protected String userName;
    protected String password;


    public User(String type, String legalName, String userName, String password) {
        this.type = type;
        this.legalName = legalName;
        this.userName = userName;
        this.password = password;
    }


    public User() {
    }

    public String getType() {
        return type;
    }

    public  String getLegalName() {
        return legalName;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    @Override
    public String marshal() {
        StringBuffer sb = new StringBuffer("type:").append(this.type).append(",");
        sb.append("legalName:").append(this.legalName).append(",");
        sb.append("userName:").append(this.userName).append(",");
        sb.append("password:").append(this.password).append(",");
        return sb.toString();
    }



    @Override
    public String toString() {
        return "Type: " + type + ", Legal name: " + legalName + ", Username: " + userName;
    }
}
