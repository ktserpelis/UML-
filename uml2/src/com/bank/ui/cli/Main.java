package com.bank.ui.cli;

import com.bank.managers.*;
import com.bank.model.users.User;
import com.bank.model.users.Admin;
import com.bank.model.users.Individual;
import com.bank.model.users.Company;

import java.time.LocalDate;
import java.util.Scanner;


public class Main {
    public static void main(String[] args) {
        LocalDate dateOfRun = LocalDate.now();
        try {
            BankAccountCreator.getInstance();

            Scanner scanner = new Scanner(System.in);

            System.out.println("Welcome to Bank Of TUC!");
            System.out.print("Username: ");
            String username = scanner.nextLine();
            System.out.print("Password: ");
            String password = scanner.nextLine();

            User user = UserManager.getInstance().authenticate(username, password);

            if (user == null) {
                System.out.println("Invalid credentials!");
                return;
            }

            if (user instanceof Admin) {
                AdminMenu.getInstance().showMenu(user, dateOfRun);
            } else if (user instanceof Individual) {
                IndividualMenu.getInstance().showMenu(user, dateOfRun);
            } else if (user instanceof Company) {
                CompanyMenu.getInstance().showMenu(user, dateOfRun);
            } else {
                System.out.println("Unknown user type!");
            }
        }finally{
            UserManager.getInstance().storeUsers();
            AccountManager.getInstance().storeBankAccounts();
            StatementManager.getInstance().storeStatements();
            System.out.println("Goodbye!!!");
        }
    }
}


