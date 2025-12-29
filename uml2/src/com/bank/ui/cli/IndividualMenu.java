package com.bank.ui.cli;

import com.bank.managers.*;
import com.bank.model.accounts.Account;
import com.bank.model.accounts.BusinessAccount;
import com.bank.model.accounts.PersonalAccount;
import com.bank.model.bills.Bill;
import com.bank.model.users.Customer;
import com.bank.model.users.Individual;
import com.bank.model.users.User;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class IndividualMenu implements ShowMenu {
    public static IndividualMenu instance;

    private IndividualMenu() {

    }

    public static IndividualMenu getInstance() {
        if (instance == null) {
            instance = new IndividualMenu();
        }
        return instance;
    }

    @Override
    public void showMenu(User user, LocalDate dateOfRun) {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("--- Individual Menu ---");
            System.out.println("1. Overview");
            System.out.println("2. Transactions");
            System.out.println("0. Logout");
            System.out.print("Choose option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    UserManager.getInstance().printCustomerDetails(user.getUserName());
                    Individual i = (Individual) user;
                    PersonalAccount a =(PersonalAccount) AccountManager.getInstance().findAccountByVat(i.getVatNumber());
                    AccountManager.getInstance().printAccountDetails(a.getIban());
                    StatementManager.getInstance().getStatementsForIban(a.getIban());

                    showMyAccounts(user);
                    break;
                case 2:
                    printTransactionsMenu(scanner,user, dateOfRun);
                    break;

                case 0:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private void printTransactionsMenu(Scanner scanner,User user, LocalDate dateOfRun) {
        while (true) {
            System.out.println("1. Withdraw");
            System.out.println("2. Deposit");
            System.out.println("3. Transfer");
            System.out.println("4. Pay Bill");
            System.out.println("0. Back");
            System.out.print("Choose option: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    withdraw(scanner,user,dateOfRun);
                    break;
                case 2:
                    deposit(scanner,user,dateOfRun);
                    break;
                case 3:
                    transfer(scanner,user,dateOfRun);
                    break;
                case 4:
                    billPaymentFromIndividual(scanner,user, dateOfRun);
                    break;
                case 0:
                    System.out.println("Return to menu!");
                    return;
                default:
                    System.out.println("Invalid option!");
            }
        }
    }

    private void deposit(Scanner scanner,User user, LocalDate dateOfRun) {
        Individual i = (Individual) user;

        System.out.print("Enter your iban: ");
        String iban = scanner.nextLine();
        String transactor = iban;

        Account acc = AccountManager.getInstance().authenticate(iban);
        if (!(acc instanceof PersonalAccount)) {
            System.out.println("Invalid iban");
            return;
        }

        PersonalAccount a = (PersonalAccount) acc;
        if (!a.whichOwner(i.getVatNumber())) {
            System.out.println("This iban doesnt belong to you");
            return;
        }

        System.out.print("Enter amount you want to deposit: ");
        try {
            double amount = scanner.nextDouble();
            scanner.nextLine();
            if (amount<=0) {
                System.out.println("Invalid amount!");
                return;
            }

            TransactionManager.getInstance().executeDeposit(a,dateOfRun,amount, transactor, "Deposit: " + amount);
        } catch (Exception e) {
            System.out.println("Invalid amount!");
        }
    }


    private void withdraw(Scanner scanner,User user, LocalDate dateOfRun) {
        Individual i = (Individual) user;

        System.out.print("Enter your iban: ");
        String iban = scanner.nextLine();
        String transactor = iban;

        Account acc = AccountManager.getInstance().authenticate(iban);
        if (!(acc instanceof PersonalAccount)) {
            System.out.println("Invalid iban");
            return;
        }

        PersonalAccount a = (PersonalAccount) acc;
        if (!a.whichOwner(i.getVatNumber())) {
            System.out.println("This iban doesnt belong to you");
            return;
        }

        System.out.print("Enter amount you want to deposit: ");
        try {
            double amount = scanner.nextDouble();
            scanner.nextLine();
            if (amount<=0) {
                System.out.println("Invalid amount!");
                return;
            }
            TransactionManager.getInstance().executeWithdrawal(a,dateOfRun ,amount, transactor, "Withdraw: " + amount);
        } catch (Exception e) {
            System.out.println("Invalid amount!");
        }
    }

    private void transfer(Scanner scanner,User user, LocalDate dateOfRun) {
        Individual i = (Individual) user;

        System.out.print("Enter your iban: ");
        String iban = scanner.nextLine();
        String transactor = iban;

        Account acc = AccountManager.getInstance().authenticate(iban);
        if (!(acc instanceof PersonalAccount)) {
            System.out.println("Invalid iban.");
            return;
        }

        PersonalAccount payer = (PersonalAccount) acc;
        if (!payer.whichOwner(i.getVatNumber())) {
            System.out.println("This iban doesnt belong to you");
            return;
        }

        System.out.print("Enter receiver's iban: ");
        String ibanReceiver = scanner.nextLine();
        Account receiver = AccountManager.getInstance().authenticate(ibanReceiver);

        if (receiver == null) {
            System.out.println("Receiver wasnt found.");
            return;
        }

        System.out.print("Enter Amount you want to tranfer: ");
        try {
            double amount = scanner.nextDouble();
            scanner.nextLine();

            if (amount <= 0) {
                System.out.println("Invalid amount");
                return;
            }

            TransactionManager.getInstance().executeTransfer(payer,dateOfRun ,receiver, amount, transactor);
        } catch (Exception e) {
            System.out.println("Invalid amount");


        }
    }

    private void billPaymentFromIndividual(Scanner scanner,User user, LocalDate dateOfRun) {
        Individual i = (Individual) user;

        System.out.print("Enter your iban: ");
        String iban = scanner.nextLine();
        String transactor = iban;

        Account acc = AccountManager.getInstance().authenticate(iban);
        if (!(acc instanceof PersonalAccount)) {
            System.out.println("Invalid iban.");
            return;
        }

        PersonalAccount payer = (PersonalAccount) acc;

        if (!payer.whichOwner(i.getVatNumber())) {
            System.out.println("This iban doesnt belong to you");
            return;
        }

        System.out.print("Enter Bill Number: ");
        String billId = scanner.nextLine();

        Bill bill = BillManager.getInstance().findIssuedBill(billId);
        if (bill == null) {
            System.out.println("Bill wasnt found.");
            return;
        }

        BusinessAccount receiver = (BusinessAccount) AccountManager.getInstance().findAccountByVat(bill.getIssuer());
        if (receiver == null) {
            System.out.println("Receiver wasnt found.");
            return;
        }

        BillManager.getInstance().payCustomerBill(bill,dateOfRun, payer, receiver,transactor);
    }

    private void showMyAccounts(User user) {
        if (!(user instanceof Customer)) {
            System.out.println("Current user is not a customer.");
            return;
        }

        Customer customer = (Customer) user;
        List<Account> myAccounts = customer.getAccounts();

        if (myAccounts.isEmpty()) {
            System.out.println("You have no accounts.");
            return;
        }

        System.out.println("=== Your Accounts ===");
        for (Account acc : myAccounts) {
            String role;
            if (acc.isPrimaryOwner(customer)) {
                role = "PRIMARY OWNER";
            } else if (acc.isCoOwner(customer)) {
                role = "CO-OWNER";
            } else {
                role = "UNKNOWN"; // δεν πρέπει να συμβεί, αλλά για σιγουριά
            }

            System.out.println("IBAN: " + acc.getIban()
                    + " | Balance: " + String.format("%.2f", acc.getBalance())
                    + " | Type: " + acc.getClass().getSimpleName()
                    + " | Role: " + role);
        }
    }

}

