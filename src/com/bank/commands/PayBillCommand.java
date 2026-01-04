package com.bank.commands;

import com.bank.managers.BillManager;
import com.bank.model.accounts.BusinessAccount;
import com.bank.model.accounts.PersonalAccount;
import com.bank.model.bills.Bill;

import java.time.LocalDate;

public class PayBillCommand implements Command {
    private final Bill bill;
    private final LocalDate date;
    private final PersonalAccount payer;
    private final BusinessAccount receiver;
    private final String transactor;

    public PayBillCommand(Bill bill, LocalDate date, PersonalAccount payer, BusinessAccount receiver, String transactor) {
        this.bill = bill;
        this.date = date;
        this.payer = payer;
        this.receiver = receiver;
        this.transactor = transactor;
    }

    @Override
    public void execute() throws Exception {
        // ✅ This will execute the transaction AND move bill to paid list + store paid.csv
        BillManager.getInstance().payCustomerBill(bill, date, payer, receiver, transactor);
    }
}
