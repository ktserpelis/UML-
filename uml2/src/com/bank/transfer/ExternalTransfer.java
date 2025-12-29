package com.bank.transfer;

public abstract class ExternalTransfer {
    protected TransferProtocol protocol;

    protected ExternalTransfer(TransferProtocol protocol) {
        this.protocol = protocol;
    }
    public abstract boolean execute(double amount, String iban, String code, String charges);

}
