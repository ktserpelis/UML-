package com.bank.ui.gui.controllers;

import com.bank.ui.gui.commands.*;
import com.bank.ui.gui.errors.ErrorBus;
import com.bank.ui.gui.facade.BankFacade;
import com.bank.ui.gui.session.AppSession;

public class DashboardController<User,AccountT,Transaction,Bill> {
    private final AppSession<User,AccountT,Transaction,Bill> session;
    private final BankFacade<User,AccountT,Transaction,Bill> facade;
    private final boolean isCompany;
    private final boolean isAdmin;

    public DashboardController(AppSession<User,AccountT,Transaction,Bill> session,
                               BankFacade<User,AccountT,Transaction,Bill> facade,
                               boolean isCompany,
                               boolean isAdmin) {
        this.session = session;
        this.facade = facade;
        this.isCompany = isCompany;
        this.isAdmin = isAdmin;
    }

    // ✅ panel can ask the controller (or DashboardPanel can keep the boolean)
    public boolean isAdmin() { return isAdmin; }
    public boolean isCompany() { return isCompany; }

    public void onSelectAccount(AccountT account) {
        if (account != null) session.setSelectedAccount(account);
    }

    public void onDeposit(String amountText) {
        AccountT acc = requireAccountOrPublish();
        if (acc == null) return;

        Double amount = parseAmountOrPublish(amountText);
        if (amount == null) return;

        new DepositCommand<>(facade, acc, amount).execute();
    }

    public void onWithdraw(String amountText) {
        AccountT acc = requireAccountOrPublish();
        if (acc == null) return;

        Double amount = parseAmountOrPublish(amountText);
        if (amount == null) return;

        new WithdrawCommand<>(facade, acc, amount).execute();
    }

    // Internal transfer
    public void onTransfer(String toIban, String amountText) {
        AccountT acc = requireAccountOrPublish();
        if (acc == null) return;

        Double amount = parseAmountOrPublish(amountText);
        if (amount == null) return;

        new TransferCommand<>(facade, acc, toIban, amount).execute();
    }

    // ✅ External transfer -> Command
    public void onExternalTransfer(String receiverIban,
                                   String code,
                                   String charges,
                                   String amountText,
                                   String protocol) {
        AccountT acc = requireAccountOrPublish();
        if (acc == null) return;

        // Keep validation here like before (same behavior)
        Double amount = parseAmountOrPublish(amountText);
        if (amount == null) return;

        if (receiverIban == null || receiverIban.isBlank()) {
            ErrorBus.getInstance().publish("Invalid input", "Receiver IBAN is empty");
            return;
        }
        if (code == null || code.isBlank()) {
            ErrorBus.getInstance().publish("Invalid input", "BIC/SWIFT is empty");
            return;
        }
        if (charges == null || charges.isBlank()) {
            ErrorBus.getInstance().publish("Invalid input", "Charges is empty");
            return;
        }
        if (protocol == null || protocol.isBlank()) {
            ErrorBus.getInstance().publish("Invalid input", "Select protocol (SEPA/SWIFT)");
            return;
        }

        new ExternalTransferCommand<>(
                facade,
                acc,
                receiverIban,
                code,
                charges,
                amount,
                protocol
        ).execute();
    }

    public void onPayRf(String rfCode) {
        AccountT acc = requireAccountOrPublish();
        if (acc == null) return;

        new PayRfBillCommand<>(facade, acc, rfCode).execute();
    }

    public void onLoadIssuedBills() {
        if (!isCompany) {
            ErrorBus.getInstance().publish("Action not allowed", "Company-only action");
            return;
        }
        new LoadIssuedBillsCommand<>(facade, session.getCurrentUser(), session).execute();
    }

    public void onAdminLoadAllAccounts() {
        new AdminLoadAllAccountsCommand<>(facade, session).execute();
    }

    public String onSimulateUntilDate(String dateText) {
        new SimulateUntilDateCommand<>(facade, dateText).execute();
        return dateText;
    }

    private AccountT requireAccountOrPublish() {
        AccountT acc = session.getSelectedAccount();
        if (acc == null) {
            ErrorBus.getInstance().publish("Action failed", "Select an account first");
            return null;
        }
        return acc;
    }

    private Double parseAmountOrPublish(String amountText) {
        if (amountText == null || amountText.isBlank()) {
            ErrorBus.getInstance().publish("Invalid input", "Amount is empty");
            return null;
        }
        try {
            return Double.parseDouble(amountText.trim());
        } catch (NumberFormatException e) {
            ErrorBus.getInstance().publish("Invalid input", "Amount must be a number");
            return null;
        }
    }

    // =================== ADMIN (for AdminPanel) ===================

    public String adminShowCustomers() {
        return facade.adminShowCustomers();
    }

    public String adminShowCustomerDetails(String vatNumber) {
        return facade.adminShowCustomerDetails(vatNumber);
    }

    public String adminShowBankAccounts() {
        return facade.adminShowBankAccounts();
    }

    public String adminShowBankAccountInfo(String iban) {
        return facade.adminShowBankAccountInfo(iban);
    }

    public String adminShowBankAccountStatements(String iban) {
        return facade.adminShowBankAccountStatements(iban);
    }

    public String adminShowIssuedCompanyBills() {
        return facade.adminShowIssuedCompanyBills();
    }

    public String adminShowPaidCompanyBills() {
        return facade.adminShowPaidCompanyBills();
    }

    public String adminLoadCompanyBills() {
        return facade.adminLoadCompanyBills();
    }

    public String adminListStandingOrders() {
        return facade.adminListStandingOrders();
    }

    public void adminPayCustomersBill(String customerIban, String billCode) {
        facade.adminPayCustomersBill(customerIban, billCode);
    }

    public void adminSimulateTimePassing(String dateUntil) {
        facade.adminSimulateTimePassing(dateUntil);
    }

}
