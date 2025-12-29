package com.bank.commands;

import com.bank.managers.BillManager;
import com.bank.model.bills.Bill;
import com.bank.model.accounts.PersonalAccount;
import com.bank.model.accounts.BusinessAccount;
import java.time.LocalDate;


public class PayBillCommand implements Command {
    private Bill bill;
    private LocalDate date;
    private PersonalAccount payer;
    private BusinessAccount receiver;
    private String transactor;

    public PayBillCommand(Bill bill, LocalDate date, PersonalAccount payer, BusinessAccount receiver, String transactor) {
        this.bill = bill;
        this.date = date;
        this.payer = payer;
        this.receiver = receiver;
        this.transactor = transactor;
    }

    @Override
    public void execute() throws Exception {
        BillManager.getInstance().payCustomerBill(bill, date, payer, receiver, transactor);
    }
}