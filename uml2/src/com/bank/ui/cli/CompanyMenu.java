package com.bank.ui.cli;

import com.bank.managers.AccountManager;
import com.bank.managers.BillManager;
import com.bank.managers.StatementManager;
import com.bank.managers.UserManager;
import com.bank.model.accounts.BusinessAccount;
import com.bank.model.users.Company;
import com.bank.model.users.User;

import java.time.LocalDate;
import java.util.Scanner;

public class CompanyMenu implements ShowMenu{
    public static CompanyMenu instance;

    private CompanyMenu() {

    }

    public static CompanyMenu getInstance() {
        if (instance == null) {
            instance = new CompanyMenu();
        }
        return instance;
    }

    @Override
    public void showMenu(User user, LocalDate date) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("--- Company Menu ---");
            System.out.println("1. Overview");
            System.out.println("2. Bills");
            System.out.println("0. Logout");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    UserManager.getInstance().printCustomerDetails(user.getUserName());
                    Company c = (Company) user;
                    BusinessAccount a =(BusinessAccount) AccountManager.getInstance().findAccountByVat(c.getVatNumber());
                    AccountManager.getInstance().printAccountDetails(a.getIban());
                    StatementManager.getInstance().getStatementsForIban(a.getIban());
                    break;
                case 2:
                    printBillMenu(scanner);
                    break;

                case 0:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private void printBillMenu(Scanner scanner) {
        while (true) {
            System.out.println("1. Load Issued Bills");
            System.out.println("2. Show Paid Bills");
            System.out.println("0. Back");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    BillManager.getInstance().loadBillsOnDate(LocalDate.now());
                    break;
                case 2:
                    BillManager.getInstance().showPaidBills();
                    break;
                case 0:
                    System.out.println("Return to menu!");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }
}