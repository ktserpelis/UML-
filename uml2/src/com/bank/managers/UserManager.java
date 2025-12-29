package com.bank.managers;

import com.bank.dao.factory.DAOFactory;
import com.bank.dao.users.UserDAO;
import com.bank.model.users.Company;
import com.bank.model.users.Customer;
import com.bank.model.users.Individual;
import com.bank.model.users.User;
import com.bank.storage.*;
import java.util.List;


public class UserManager {
    private static UserManager instance;

    private final UserDAO userDAO;
    private final StorableList<User> users;



    private UserManager() {
        // Παίρνουμε τη DAOFactory για File System (CSV)
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.FS);
        this.userDAO = factory.getUserDAO();

        // Φορτώνουμε όλους τους χρήστες από το DAO
        this.users = userDAO.findAll();
    }

    public static UserManager getInstance() {
        if (instance == null) {
            instance = new UserManager();
        }
        return instance;
    }


    public void storeUsers() {
        userDAO.saveAll(users);
    }

    // ========== BUSINESS LOGIC ==========

    public User authenticate(String userName, String password) {
        for (User user : users) {
            if (user.getUserName().equals(userName) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }

    public User findUserByUsername(String userName) {
        for (User user : users) {
            if (user!=null && user.getUserName().equals(userName)) {
                System.out.println("Found user: " + user.getUserName());
                return user;
            }
        }
        return null;
    }

    public User findCustomerByVat(String vatNumber) {
        for (User user : users) {
            if (user instanceof Customer) {
                Customer customer = (Customer) user;
                if (customer.getVatNumber().equals(vatNumber)) {
                    return customer;
                }
            }
        }
        return null;
    }

    public void addUser(User user) {
        for(User u:users){
            if(u.getUserName().equals(user.getUserName())){
                return;
            }
        }
        users.add(user);
    }

    public List<User> getUsers() {
        return users;
    }


    public void showCustomers(){
        System.out.println("-- Individual Customers --");
        for (User user : users) {
            if (user instanceof Individual) {
                System.out.println(user);
            }
        }

        System.out.println("-- Company Customers --");
        for (User user : users) {
            if (user instanceof Company) {
                System.out.println(user);
            }
        }
    }
    public void printCustomerDetails(String username) {
        User u = UserManager.getInstance().findUserByUsername(username);
        System.out.println("Customer Info: " + u.toString());
    }
}

