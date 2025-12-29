package com.bank.transfer;

public class BankTransfer extends ExternalTransfer {
    public BankTransfer(TransferProtocol protocol) {
        super(protocol);
    }

    @Override
    public boolean execute(double amount, String iban, String code, String charges) {
        //η execute επιστρέφει το αποτέλεσμα της "γέφυρας"
        return protocol.processTransfer(amount, iban, code, charges);
    }
}