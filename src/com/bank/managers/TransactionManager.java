package com.bank.managers;

import com.bank.exceptions.BankException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.model.accounts.Account;
import com.bank.model.accounts.BusinessAccount;
import com.bank.model.accounts.PersonalAccount;
import com.bank.model.bills.Bill;
import com.bank.transfer.ExternalTransfer;

import java.time.LocalDate;

public class TransactionManager {
    private static TransactionManager instance;

    private TransactionManager() {}

    public static TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }

    public void executeDeposit(Account a, LocalDate date, double amount, String transactor, String description) {
        try {
            a.deposit(amount);

            StatementManager.getInstance().createIndividualStatement("Statement", date, amount,
                    description + amount, transactor, a.getIban(),
                    "None", a.getBalance());

        } catch (Exception e) {
            // propagate to GUI layer
            throw new BankException("Deposit failed: " + e.getMessage(), e);
        }
    }

    public void executeWithdraw(Account a, LocalDate date, double amount, String transactor, String description) {
        try {
            if (a.getBalance() < amount) {
                // user-facing error
                throw new InsufficientFundsException("Insufficient balance to withdraw " + amount);
            }

            a.withdraw(amount);

            StatementManager.getInstance().createIndividualStatement("Statement", date, amount,
                    description + amount, transactor, a.getIban(),
                    "None", a.getBalance());

            AccountManager.getInstance().storeBankAccounts();

        } catch (InsufficientFundsException e) {
            throw e; // keep clean message
        } catch (Exception e) {
            throw new BankException("Withdrawal failed: " + e.getMessage(), e);
        }
    }

    public void executeTransfer(Account sender, LocalDate date, Account receiver, double amount, String transactor) {
        try {
            if (sender.getBalance() < amount) {
                throw new InsufficientFundsException("Insufficient balance for transfer");
            }

            sender.withdraw(amount);
            receiver.deposit(amount);

            StatementManager.getInstance().createIndividualStatement("Statement", date, amount,
                    "Transfer to: " + receiver.getIban(), transactor, sender.getIban(),
                    receiver.getIban(), sender.getBalance());

            StatementManager.getInstance().createCompanyStatement("Statement", date, amount,
                    "Transfer from: " + sender.getIban(), transactor, sender.getIban(),
                    receiver.getIban(), receiver.getBalance());

        } catch (InsufficientFundsException e) {
            throw e;
        } catch (Exception e) {
            throw new BankException("Transfer failed: " + e.getMessage(), e);
        }
    }

    public void executeExternalTransfer(Account sender, LocalDate date, String receiverIban, String bankCode,
                                        double amount, String charges, ExternalTransfer bridge, String transactor) {
        try {
            if (sender.getBalance() < amount) {
                throw new InsufficientFundsException("Insufficient balance for external transfer");
            }

            boolean success = bridge.execute(amount, receiverIban, bankCode, charges);
            if (!success) {
                // still user-facing, but not funds-related
                throw new BankException("External transfer failed");
            }

            sender.withdraw(amount);

            StatementManager.getInstance().createIndividualStatement(
                    "Statement", date, amount, "External transfer via " + bridge.getProtocolName(), transactor,
                    sender.getIban(), receiverIban, sender.getBalance());

        } catch (InsufficientFundsException e) {
            throw e;
        } catch (BankException e) {
            throw e;
        } catch (Exception e) {
            throw new BankException("External transfer error: " + e.getMessage(), e);
        }
    }

    public void executeBills(Bill bill, LocalDate date, PersonalAccount payer, BusinessAccount receiver, String transactor) {
        try {
            if (payer.getBalance() < bill.getAmount()) {
                throw new InsufficientFundsException("Insufficient balance to pay the bill");
            }

            payer.withdraw(bill.getAmount());
            receiver.deposit(bill.getAmount());

            StatementManager.getInstance().createIndividualStatement("Statement", date, bill.getAmount(),
                    "Bill Payment for " + receiver.getIban(), transactor, payer.getIban(),
                    receiver.getIban(), payer.getBalance());

            StatementManager.getInstance().createCompanyStatement("Statement", date, bill.getAmount(),
                    "Bill: " + bill.getBillNumber() + "From: " + payer.getIban(), transactor, payer.getIban(),
                    receiver.getIban(), receiver.getBalance());

        } catch (InsufficientFundsException e) {
            throw e;
        } catch (Exception e) {
            throw new BankException("Bill payment failed: " + e.getMessage(), e);
        }
    }
}
