package com.bank.managers;

import com.bank.model.accounts.Account;
import com.bank.model.accounts.BusinessAccount;
import com.bank.model.accounts.PersonalAccount;
import com.bank.model.bills.Bill;
import com.bank.model.transactions.*;
import com.bank.transfer.TransferProtocol;
import com.bank.patterns.observer.Subject;
import com.bank.patterns.observer.TransactionEvent;
import com.bank.patterns.observer.TransactionObserver;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;


public class TransactionManager implements Subject {
    private static TransactionManager instance;
    private Queue<Transaction> transactionQueue;
    private final List<TransactionObserver> observers = new ArrayList<>();

    private TransactionManager() {
        this.transactionQueue = new LinkedList<>();
    }

    public static TransactionManager getInstance() {
        if (instance == null) {
            instance = new TransactionManager();
        }
        return instance;
    }

    @Override
    public void addObserver(TransactionObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(TransactionObserver observer) {
        observers.remove(observer);
    }

    @Override
    public void notifyObservers(TransactionEvent event) {
        for (TransactionObserver observer : observers) {
            observer.onTransactionExecuted(event);
        }
    }

    public void addTransaction(Transaction t) {
        transactionQueue.add(t);
    }

    public void executePendingTransactions() {
        while (!transactionQueue.isEmpty()) {
            Transaction t = transactionQueue.poll();
            t.execute();
        }
    }

    public void executeDeposit(Account a, LocalDate date ,double amount, String transactor, String description ) {
        Deposit deposit = new Deposit(a, amount, transactor, description, date);
        deposit.execute();
    }

    public void executeWithdrawal(Account a, LocalDate date, double amount, String transactor, String description) {
        Withdrawal withdrawal = new Withdrawal(a, amount, transactor, description, date);
        withdrawal.execute();
    }

    public void executeTransfer(Account sender, LocalDate date, Account receiver, double amount, String transactor) {
        Transfer transfer = new Transfer(sender, receiver, amount, transactor, "Transfer", date);
        transfer.execute();
    }

    public void executeTransferWithProtocol(Account sender, LocalDate date, Account receiver, double amount, String transactor, TransferProtocol protocol) {
        Transfer transfer = new Transfer(sender, receiver, amount, transactor, "Transfer", date, protocol);
        transfer.execute();
    }


    public void executeBills(Bill bill, LocalDate date, PersonalAccount payer, BusinessAccount receiver,String transactor) {
        Payment payment = new Payment(payer, receiver, bill, transactor, "Bill Payment", date);
        payment.execute();
    }
}