package com.bank.transfer;

public interface TransferProtocol {
    boolean processTransfer(double amount, String iban, String bicOrSwift, String charges);
}
