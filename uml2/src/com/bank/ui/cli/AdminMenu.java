package com.bank.ui.cli;

import java.time.LocalDate;
import java.util.Scanner;
import com.bank.managers.*;
import com.bank.model.accounts.Account;
import com.bank.model.accounts.BusinessAccount;
import com.bank.model.accounts.PersonalAccount;
import com.bank.model.bills.Bill;
import com.bank.model.users.Customer;
import com.bank.model.users.User;

public class AdminMenu implements ShowMenu {
    private static AdminMenu instance;

    private AdminMenu() {
    }

    public static AdminMenu getInstance() {
        if (instance == null) {
            instance = new AdminMenu();
        }
        return instance;
    }


    @Override
    public void showMenu(User user, LocalDate date) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("--- Admin Menu ---");
            System.out.println("1. Show Customers");
            System.out.println("2. Show Customer Details");
            System.out.println("3. Show Bank Accounts");
            System.out.println("4. Show Bank Account Info");
            System.out.println("5. Show Bank Account Statements");
            System.out.println("6. Show Issued Company Bills");
            System.out.println("7. Show Paid Company Bills");
            System.out.println("8. Load Company Bills");
            System.out.println("9. List Standing Orders");
            System.out.println("10. Pay Customer’s Bill");
            System.out.println("11. Simulate Time Passing");
            System.out.println("0. Logout");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    UserManager.getInstance().showCustomers();
                    break;
                case 2:
                    System.out.print("Enter vat number: ");
                    String vatNumber = scanner.nextLine();
                    Customer customer = (Customer) UserManager.getInstance().findCustomerByVat(vatNumber);
                    if (customer != null) {
                        System.out.println(customer.toString());
                    } else {
                        System.out.println("Customer wasnt found.");
                    }
                    break;
                case 3:
                    AccountManager.getInstance().showBankAccounts();
                    break;
                case 4:
                    System.out.print("Enter Iban: ");
                    String iban = scanner.nextLine();
                    Account a =  AccountManager.getInstance().findAccountByIban(iban);

                    if(a instanceof BusinessAccount) {
                        System.out.println(a.toString());
                    }
                    else if(a instanceof PersonalAccount) {
                        System.out.println(a.toString());
                    }else {
                        System.out.println("Account wasnt found.");
                    }
                    break;
                case 5:
                    System.out.print("Enter iban: ");
                    String i = scanner.nextLine();
                    Account acc =  AccountManager.getInstance().authenticate(i);
                    if (acc == null) {
                        System.out.println("Iban wasnt found.");
                        continue;
                    }

                    StatementManager.getInstance().showStatements(i);
                    break;
                case 6:
                    BillManager.getInstance().showIssuedBills();
                    break;
                case 7:
                    BillManager.getInstance().showPaidBills();
                    break;
                case 8:
                    BillManager.getInstance().loadBillsOnDate(LocalDate.parse("2025-05-01"));
                    break;
                case 9:
                    StandingOrderManager.getInstance().showActiveOrders();
                    break;
                case 10:
                    billPaymentFromAdmin(scanner, LocalDate.now());
                    break;
                case 11:
                    simulateTimePassing(scanner);
                    break;
                case 0:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }


    //10
    private void billPaymentFromAdmin(Scanner scanner, LocalDate date) {
        String transactor = "Admin";
        System.out.print("Enter Customer iban: ");
        String iban = scanner.nextLine();

        System.out.print("Enter Bill Code: ");
        String billnum = scanner.nextLine();

        Bill bill = BillManager.getInstance().findIssuedBill(billnum);
        if (bill == null) {
            System.out.println("Bill wasnt found.");
            return;
        }

        PersonalAccount payer=(PersonalAccount) AccountManager.getInstance().authenticate(iban);
        if (payer == null) {
            System.out.println("Customer wasnt found.");
            return;
        }

        BusinessAccount receiver=(BusinessAccount) AccountManager.getInstance().findAccountByVat(bill.getIssuer());
        if (receiver == null) {
            System.out.println("Receiver wasnt found.");
            return;
        }

        BillManager.getInstance().payCustomerBill(bill,date, payer, receiver,transactor);
    }

    //11
    private void simulateTimePassing(Scanner scanner) {
        System.out.print("Enter date until you want to simulate (YYYY-MM-DD): ");
        String dateUntil = scanner.nextLine();
        try {

            LocalDate date = LocalDate.parse(dateUntil);
            TimeSimulator.getInstance().simulateUntil(dateUntil);
        } catch (Exception e) {
            System.out.println("Error during simulation: " + e.getMessage());
        }
    }
}