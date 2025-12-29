package com.bank.patterns.observer;

public interface Subject {
    void addObserver(TransactionObserver observer);
    void removeObserver(TransactionObserver observer);
    void notifyObservers(TransactionEvent event);
}
