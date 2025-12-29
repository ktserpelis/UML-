package com.bank.patterns.observer;

public interface TransactionObserver {
    void onTransactionExecuted(TransactionEvent event);
}
