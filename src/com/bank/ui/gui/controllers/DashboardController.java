package com.bank.ui.gui.controllers;

import com.bank.model.orders.StandingOrder;
import com.bank.ui.gui.commands.*;
import com.bank.ui.gui.errors.ErrorBus;
import com.bank.ui.gui.facade.BankFacade;
import com.bank.ui.gui.session.AppSession;

import java.util.List;

public class DashboardController<User,AccountT,Transaction,Bill> {
    private final AppSession<User,AccountT,Transaction,Bill> session;
    private final BankFacade<User,AccountT,Transaction,Bill> facade;
    private final boolean isCompany;
    private final boolean isAdmin;
    private final Runnable onLogoutShowLogin;

    public DashboardController(AppSession<User,AccountT,Transaction,Bill> session,
                               BankFacade<User,AccountT,Transaction,Bill> facade,
                               boolean isCompany,
                               boolean isAdmin,
                               Runnable onLogoutShowLogin) {
        this.session = session;
        this.facade = facade;
        this.isCompany = isCompany;
        this.isAdmin = isAdmin;
        this.onLogoutShowLogin = onLogoutShowLogin;
    }

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

    public void onTransfer(String toIban, String amountText) {
        AccountT acc = requireAccountOrPublish();
        if (acc == null) return;

        Double amount = parseAmountOrPublish(amountText);
        if (amount == null) return;

        new TransferCommand<>(facade, acc, toIban, amount).execute();
    }

    public void onExternalTransfer(String receiverIban,
                                   String code,
                                   String charges,
                                   String amountText,
                                   String protocol) {
        AccountT acc = requireAccountOrPublish();
        if (acc == null) return;

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

    // =================== STANDING ORDERS (GUI) ===================

    public List<StandingOrder> getMyStandingOrders() {
        try {
            return facade.getMyStandingOrders();
        } catch (Exception e) {
            ErrorBus.getInstance().publish("Standing orders", e.getMessage());
            return List.of();
        }
    }

    public String onCreateTransferStandingOrder(String title,
                                                String description,
                                                String chargeIban,
                                                String creditIban,
                                                String amountText,
                                                String startDate,
                                                String endDate,
                                                String freqText,
                                                String dayText) {
        if (chargeIban == null || chargeIban.isBlank()) {
            ErrorBus.getInstance().publish("Invalid input", "Charge IBAN is empty");
            return null;
        }
        if (creditIban == null || creditIban.isBlank()) {
            ErrorBus.getInstance().publish("Invalid input", "Credit IBAN is empty");
            return null;
        }
        if (startDate == null || startDate.isBlank() || endDate == null || endDate.isBlank()) {
            ErrorBus.getInstance().publish("Invalid input", "Start/End date is empty");
            return null;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountText.trim());
        } catch (Exception e) {
            ErrorBus.getInstance().publish("Invalid input", "Amount must be a number");
            return null;
        }
        if (amount <= 0) {
            ErrorBus.getInstance().publish("Invalid input", "Amount must be > 0");
            return null;
        }

        int freq;
        try {
            freq = Integer.parseInt(freqText.trim());
        } catch (Exception e) {
            ErrorBus.getInstance().publish("Invalid input", "Frequency must be an integer");
            return null;
        }
        if (freq <= 0) {
            ErrorBus.getInstance().publish("Invalid input", "Frequency must be >= 1");
            return null;
        }

        int day;
        try {
            day = Integer.parseInt(dayText.trim());
        } catch (Exception e) {
            ErrorBus.getInstance().publish("Invalid input", "Day of month must be an integer");
            return null;
        }
        if (day < 1 || day > 28) {
            ErrorBus.getInstance().publish("Invalid input", "Day of month must be 1..28");
            return null;
        }

        return new CreateTransferStandingOrderCommand<>(
                facade,
                title,
                description,
                chargeIban,
                creditIban,
                amount,
                startDate,
                endDate,
                freq,
                day
        ).execute();
    }

    public String onCreatePaymentStandingOrder(String title,
                                               String description,
                                               String chargeIban,
                                               String paymentCode,
                                               String maxAmountText,
                                               String startDate,
                                               String endDate) {

        if (chargeIban == null || chargeIban.isBlank()) {
            ErrorBus.getInstance().publish("Invalid input", "Charge IBAN is empty");
            return null;
        }
        if (paymentCode == null || paymentCode.isBlank()) {
            ErrorBus.getInstance().publish("Invalid input", "Payment code is empty");
            return null;
        }
        if (startDate == null || startDate.isBlank() || endDate == null || endDate.isBlank()) {
            ErrorBus.getInstance().publish("Invalid input", "Start/End date is empty");
            return null;
        }

        double maxAmount;
        try {
            maxAmount = Double.parseDouble(maxAmountText.trim());
        } catch (Exception e) {
            ErrorBus.getInstance().publish("Invalid input", "Max amount must be a number");
            return null;
        }
        if (maxAmount <= 0) {
            ErrorBus.getInstance().publish("Invalid input", "Max amount must be > 0");
            return null;
        }

        return new CreatePaymentStandingOrderCommand<>(
                facade,
                title,
                description,
                chargeIban,
                paymentCode,
                maxAmount,
                startDate,
                endDate
        ).execute();
    }

    // BILL CREATION
    public String onCompanyCreateBill(String customerVat,
                                      String amountText,
                                      String issueDate,
                                      String dueDate,
                                      String paymentCode,
                                      String billNumber) {

        if (!isCompany) {
            ErrorBus.getInstance().publish("Action not allowed", "Only companies can create bills");
            return null;
        }
        if (customerVat == null || customerVat.isBlank()) {
            ErrorBus.getInstance().publish("Invalid input", "Customer VAT is empty");
            return null;
        }
        if (amountText == null || amountText.isBlank()) {
            ErrorBus.getInstance().publish("Invalid input", "Amount is empty");
            return null;
        }
        if (issueDate == null || issueDate.isBlank() || dueDate == null || dueDate.isBlank()) {
            ErrorBus.getInstance().publish("Invalid input", "Issue/Due date is empty");
            return null;
        }

        return new CreateCompanyBillCommand<User,AccountT,Transaction,Bill>(
                facade,
                customerVat,
                amountText,
                issueDate,
                dueDate,
                paymentCode,
                billNumber
        ).execute();
    }


    // =================== ADMIN (for AdminPanel) ===================

    public String adminShowCustomers() { return facade.adminShowCustomers(); }
    public String adminShowCustomerDetails(String vatNumber) { return facade.adminShowCustomerDetails(vatNumber); }
    public String adminShowBankAccounts() { return facade.adminShowBankAccounts(); }
    public String adminShowBankAccountInfo(String iban) { return facade.adminShowBankAccountInfo(iban); }
    public String adminShowBankAccountStatements(String iban) { return facade.adminShowBankAccountStatements(iban); }
    public String adminShowIssuedCompanyBills() { return facade.adminShowIssuedCompanyBills(); }
    public String adminShowPaidCompanyBills() { return facade.adminShowPaidCompanyBills(); }
    public String adminLoadCompanyBills() { return facade.adminLoadCompanyBills(); }
    public String adminListStandingOrders() { return facade.adminListStandingOrders(); }
    public void adminPayCustomersBill(String customerIban, String billCode) { facade.adminPayCustomersBill(customerIban, billCode); }
    public String adminSimulateTimePassing(String dateUntil) { return facade.adminSimulateTimePassing(dateUntil); }

    public void onLogout() {
        facade.logout();
        if (onLogoutShowLogin != null) onLogoutShowLogin.run();
    }
}
