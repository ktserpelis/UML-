package com.bank.ui.gui.facade;

import com.bank.model.orders.StandingOrder;
import com.bank.ui.gui.errors.ErrorBus;
import com.bank.ui.gui.errors.NotificationBus;
import com.bank.ui.gui.session.AppSession;

import java.lang.reflect.Method;
import java.util.List;

public class BankFacadeImpl<U, A, T, B> implements BankFacade<U, A, T, B> {

    private final BackendPort<U, A, T, B> backend;

    // We keep a reference so actions can refresh the GUI state.
    private AppSession<U, A, T, B> session;

    public BankFacadeImpl(BackendPort<U, A, T, B> backend) {
        if (backend == null) throw new IllegalArgumentException("backend is null");
        this.backend = backend;
    }

    @Override
    public void onUserSelected(U user, AppSession<U, A, T, B> session) {
        if (session == null) throw new IllegalArgumentException("session is null");
        this.session = session;

        List<A> accounts = backend.getAccountsForUser(user);
        List<T> transactions = backend.getTransactionsForUser(user);
        List<B> bills = backend.getBillsForUser(user);

        session.setCurrentUser(user);
        session.clearUserScopedData();
        session.setAccounts(accounts);
        session.setTransactions(transactions);
        session.setBills(bills);

        if (accounts != null && !accounts.isEmpty()) {
            session.setSelectedAccount(accounts.get(0));
        } else {
            session.setSelectedAccount(null);
        }

        // ✅ success notification (optional)
        if (!ErrorBus.getInstance().hadError()) {
            NotificationBus.getInstance().publish("Login", "Welcome!");
        }
    }

    @Override
    public void deposit(A account, double amount) {
        backend.deposit(account, amount);
        refreshSessionIfPossible();

        if (!ErrorBus.getInstance().hadError()) {
            NotificationBus.getInstance().publish(
                    "Deposit successful",
                    "Deposited " + amount
            );
        }
    }

    @Override
    public void withdraw(A account, double amount) {
        backend.withdraw(account, amount);
        refreshSessionIfPossible();

        if (!ErrorBus.getInstance().hadError()) {
            NotificationBus.getInstance().publish(
                    "Withdrawal successful",
                    "Withdrew " + amount
            );
        }
    }

    @Override
    public void transfer(A fromAccount, String toIban, double amount) {
        backend.transfer(fromAccount, toIban, amount);
        refreshSessionIfPossible();

        if (!ErrorBus.getInstance().hadError()) {
            NotificationBus.getInstance().publish(
                    "Transfer successful",
                    "Transferred " + amount + " to " + toIban
            );
        }
    }

    @Override
    public void externalTransfer(A fromAccount,
                                 String receiverIban,
                                 String code,
                                 String charges,
                                 double amount,
                                 String protocol) {
        backend.externalTransfer(fromAccount, receiverIban, code, charges, amount, protocol);
        refreshSessionIfPossible();

        if (!ErrorBus.getInstance().hadError()) {
            NotificationBus.getInstance().publish(
                    "External transfer successful",
                    "Sent " + amount + " to " + receiverIban + " (" + protocol + ")"
            );
        }
    }

    @Override
    public void payRfBill(A fromAccount, String rfCode) {
        backend.payRfBill(fromAccount, rfCode);
        refreshSessionIfPossible();

        if (!ErrorBus.getInstance().hadError()) {
            NotificationBus.getInstance().publish(
                    "Bill payment successful",
                    "Paid bill with code " + rfCode
            );
        }
    }

    @Override
    public void loadIssuedBills(U companyUser, AppSession<U, A, T, B> session) {
        if (session == null) throw new IllegalArgumentException("session is null");

        List<B> issued = backend.loadIssuedBills(companyUser);
        session.setBills(issued);

        if (!ErrorBus.getInstance().hadError()) {
            NotificationBus.getInstance().publish(
                    "Bills loaded",
                    "Issued bills loaded successfully."
            );
        }
    }

    @Override
    public U login(String username, String password, AppSession<U, A, T, B> session) {
        if (session == null) throw new IllegalArgumentException("session is null");

        U user = backend.login(username, password);
        if (user != null) {
            onUserSelected(user, session);
        }
        return user;
    }

    @Override
    public void loadAllAccounts(AppSession<U,A,T,B> session) {
        session.setAccounts(backend.getAllAccounts());

        if (!ErrorBus.getInstance().hadError()) {
            NotificationBus.getInstance().publish(
                    "Admin",
                    "Loaded all accounts."
            );
        }
    }

    @Override
    public String adminShowCustomers() {
        return backend.adminShowCustomers();
    }

    @Override
    public String adminShowCustomerDetails(String vatNumber) {
        return backend.adminShowCustomerDetails(vatNumber);
    }

    @Override
    public String adminShowBankAccounts() {
        return backend.adminShowBankAccounts();
    }

    @Override
    public String adminShowBankAccountInfo(String iban) {
        return backend.adminShowBankAccountInfo(iban);
    }

    @Override
    public String adminShowBankAccountStatements(String iban) {
        return backend.adminShowBankAccountStatements(iban);
    }

    @Override
    public String adminShowIssuedCompanyBills() {
        return backend.adminShowIssuedCompanyBills();
    }

    @Override
    public String adminShowPaidCompanyBills() {
        return backend.adminShowPaidCompanyBills();
    }

    @Override
    public String adminLoadCompanyBills() {
        String res = backend.adminLoadCompanyBills();
        refreshSessionIfPossible();

        if (!ErrorBus.getInstance().hadError()) {
            NotificationBus.getInstance().publish(
                    "Admin",
                    "Company bills loaded."
            );
        }
        return res;
    }

    @Override
    public String adminListStandingOrders() {
        return backend.adminListStandingOrders();
    }

    @Override
    public void adminPayCustomersBill(String customerIban, String billCode) {
        backend.adminPayCustomersBill(customerIban, billCode);
        refreshSessionIfPossible();

        if (!ErrorBus.getInstance().hadError()) {
            NotificationBus.getInstance().publish(
                    "Admin payment",
                    "Paid bill " + billCode + " from " + customerIban
            );
        }
    }

    @Override
    public String adminSimulateTimePassing(String dateUntil) {
        String log = backend.adminSimulateTimePassing(dateUntil);
        refreshSessionIfPossible();

        if (!ErrorBus.getInstance().hadError()) {
            NotificationBus.getInstance().publish(
                    "Simulation finished",
                    "Simulation completed until " + dateUntil
            );
        }
        return log;
    }

    @Override
    public void logout() {
        session.setCurrentUser(null);
        session.clearUserScopedData();

        // ✅ success notify
        NotificationBus.getInstance().publish("Logout", "You have been logged out.");
    }

    @Override
    public List<StandingOrder> getMyStandingOrders() {
        String vat = getVat(session.getCurrentUser());
        return backend.getStandingOrdersForCustomer(vat);
    }

    @Override
    public String createTransferStandingOrder(String title,
                                              String description,
                                              String chargeIban,
                                              String creditIban,
                                              double amount,
                                              String startDate,
                                              String endDate,
                                              int frequencyInMonths,
                                              int dayOfMonth) {

        String vat = getVat(session.getCurrentUser());
        String msg = backend.createTransferStandingOrder(
                vat, title, description, chargeIban, creditIban,
                amount, startDate, endDate, frequencyInMonths, dayOfMonth
        );

        if (!ErrorBus.getInstance().hadError()) {
            NotificationBus.getInstance().publish(
                    "Standing order created",
                    "Transfer order created successfully."
            );
        }

        return msg;
    }

    @Override
    public String createPaymentStandingOrder(String title,
                                             String description,
                                             String chargeIban,
                                             String paymentCode,
                                             double maxAmount,
                                             String startDate,
                                             String endDate) {

        String vat = getVat(session.getCurrentUser());
        String msg = backend.createPaymentStandingOrder(
                vat, title, description, chargeIban, paymentCode,
                maxAmount, startDate, endDate
        );

        if (!ErrorBus.getInstance().hadError()) {
            NotificationBus.getInstance().publish(
                    "Standing order created",
                    "Payment order created successfully."
            );
        }

        return msg;
    }

    @Override
    public String companyCreateBill(String customerVat,
                                    String amountText,
                                    String issueDate,
                                    String dueDate,
                                    String paymentCode,
                                    String billNumber) {

        String issuerVat = getVat(session.getCurrentUser());
        if (issuerVat.isBlank())
            throw new IllegalStateException("Cannot resolve company VAT");

        double amount;
        try {
            amount = Double.parseDouble(amountText.trim());
        } catch (Exception e) {
            throw new IllegalArgumentException("Amount must be a number");
        }

        String msg = backend.companyCreateBill(
                issuerVat,
                customerVat,
                amount,
                issueDate,
                dueDate,
                paymentCode,
                billNumber
        );

        if (!ErrorBus.getInstance().hadError()) {
            NotificationBus.getInstance().publish(
                    "Bill created",
                    "Bill was created successfully."
            );
        }

        return msg;
    }

    private String getVat(Object user) {
        if (user == null) throw new IllegalStateException("No logged in user");

        String[] methods = {"getVatNumber", "getVat", "getCustomerVat", "getId", "getCustomerId"};

        for (String m : methods) {
            try {
                Method method = user.getClass().getMethod(m);
                Object val = method.invoke(user);
                if (val instanceof String s && !s.isBlank()) return s.trim();
            } catch (Exception ignored) {}
        }

        String[] fields = {"vatNumber", "vat", "customerVat", "id", "customerId"};

        for (String f : fields) {
            try {
                var field = user.getClass().getDeclaredField(f);
                field.setAccessible(true);
                Object val = field.get(user);
                if (val instanceof String s && !s.isBlank()) return s.trim();
            } catch (Exception ignored) {}
        }

        throw new IllegalStateException("Cannot extract VAT from user: " + user.getClass().getName());
    }

    private void refreshSessionIfPossible() {
        if (session == null) return;
        U user = session.getCurrentUser();
        if (user == null) return;

        List<A> accounts = backend.getAccountsForUser(user);
        List<T> transactions = backend.getTransactionsForUser(user);
        List<B> bills = backend.getBillsForUser(user);

        session.setAccounts(accounts);
        session.setTransactions(transactions);
        session.setBills(bills);

        A selected = session.getSelectedAccount();
        if (selected == null && accounts != null && !accounts.isEmpty()) {
            session.setSelectedAccount(accounts.get(0));
        }
    }
}
