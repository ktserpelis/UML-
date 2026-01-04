package com.bank.ui.gui.facade;

import com.bank.exceptions.BankException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.managers.*;
import com.bank.model.accounts.Account;
import com.bank.model.accounts.BusinessAccount;
import com.bank.model.accounts.PersonalAccount;
import com.bank.model.bills.Bill;
import com.bank.model.orders.StandingOrder;
import com.bank.model.statements.Statement;
import com.bank.model.users.Company;
import com.bank.model.users.Customer;
import com.bank.model.users.User;
import com.bank.ui.gui.errors.ErrorBus;
import com.bank.model.users.Individual;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class BackendPortImpl implements BackendPort<User, Account, Statement, Bill> {

    private final AccountManager accountManager;
    private final TransactionManager transactionManager;
    private final BillManager billManager;
    private final StatementManager statementManager;

    public BackendPortImpl() {
        this.accountManager = AccountManager.getInstance();
        this.transactionManager = TransactionManager.getInstance();
        this.billManager = BillManager.getInstance();
        this.statementManager = StatementManager.getInstance(); // registers as observer
    }

    // ------------------ Option B helper ------------------

    private void runSafely(String actionName, Runnable action) {
        try {
            action.run();
        } catch (InsufficientFundsException e) {
            ErrorBus.getInstance().publish("Not enough balance", e.getMessage());
        } catch (IllegalArgumentException e) {
            ErrorBus.getInstance().publish("Invalid input", e.getMessage());
        } catch (BankException e) {
            ErrorBus.getInstance().publish(actionName + " failed", e.getMessage());
        } catch (Exception e) {
            ErrorBus.getInstance().publish("Unexpected error", e.getMessage());
        }
    }

    @Override
    public boolean isCompany(User user) {
        return user instanceof Company
                || (user.getType() != null && user.getType().equalsIgnoreCase("company"));
    }

    @Override
    public List<Account> getAccountsForUser(User user) {
        if (user instanceof Customer c) {
            return c.getAccounts();
        }
        return List.of();
    }

    @Override
    public List<Statement> getTransactionsForUser(User user) {
        if (!(user instanceof Customer c)) {
            return List.of();
        }

        List<Account> accounts = c.getAccounts();
        if (accounts == null || accounts.isEmpty()) {
            return List.of();
        }

        List<Statement> all = new ArrayList<>();

        for (Account acc : accounts) {
            if (acc == null || acc.getIban() == null) continue;

            // Ensure statements exist in memory (loads from file into the map)
            statementManager.loadStatementsFromFile(acc.getIban());

            var list = statementManager.getStatementsForIban(acc.getIban());
            if (list != null && !list.isEmpty()) {
                all.addAll(list);
            }
        }

        // Sort newest first (Statement has no getters, so read "date" reflectively)
        all.sort((s1, s2) -> {
            LocalDate d1 = extractStatementDate(s1);
            LocalDate d2 = extractStatementDate(s2);
            if (d1 == null && d2 == null) return 0;
            if (d1 == null) return 1;
            if (d2 == null) return -1;
            return d2.compareTo(d1);
        });

        return all;
    }


    @Override
    public User login(String username, String password) {
        // ΔΕΝ κάνουμε runSafely εδώ (γιατί θέλουμε να επιστρέψουμε User ή null)
        if (username == null || username.isBlank()) throw new IllegalArgumentException("Username is empty");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password is empty");

        // CLI-style: επιστρέφει null αν invalid
        return UserManager.getInstance().authenticate(username.trim(), password);
    }

    private LocalDate extractStatementDate(Statement s) {
        try {
            var f = Statement.class.getDeclaredField("date");
            f.setAccessible(true);
            return (LocalDate) f.get(s);
        } catch (Exception ignored) {
            return null;
        }
    }

    @Override
    public List<Bill> getBillsForUser(User user) {
        var issued = billManager.getIssuedBills();
        if (issued == null || issued.isEmpty() || user == null) return List.of();

        String userVat = tryGetUserVat(user);
        if (userVat == null || userVat.isBlank()) return List.of();

        List<Bill> result = new ArrayList<>();

        if (isCompany(user)) {
            for (Bill b : issued) {
                String companyVat = tryGetBillCompanyVat(b);
                if (companyVat != null && companyVat.equals(userVat) && !b.getPaid()) {
                    result.add(b);
                }
            }
        } else {
            for (Bill b : issued) {
                String customerVat = tryGetBillCustomerVat(b);
                if (customerVat != null && customerVat.equals(userVat) && !b.getPaid()) {
                    result.add(b);
                }
            }
        }
        return result;
    }

    @Override
    public void deposit(Account account, double amount) {
        runSafely("Deposit", () -> {
            if (account == null) throw new IllegalArgumentException("Account is null");
            if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");

            LocalDate today = LocalDate.now();
            String transactor = (account.getOwner() != null && account.getPrimaryOwnerVat() != null)
                    ? account.getPrimaryOwnerVat()
                    : "GUI";

            transactionManager.executeDeposit(account, today, amount, transactor, "Deposit");

            accountManager.storeBankAccounts();
            statementManager.storeStatements();
        });
    }

    @Override
    public void withdraw(Account account, double amount) {
        runSafely("Withdraw", () -> {
            if (account == null) throw new IllegalArgumentException("Account is null");
            if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");

            LocalDate today = LocalDate.now();
            String transactor = (account.getOwner() != null && account.getOwner().getUserName() != null)
                    ? account.getOwner().getUserName()
                    : "GUI";

            // IMPORTANT: your uploaded TransactionManager uses executeWithdrawal()
            transactionManager.executeWithdraw(account, today, amount, transactor, "Withdrawal");

            accountManager.storeBankAccounts();
            statementManager.storeStatements();
        });
    }

    @Override
    public void transfer(Account fromAccount, String toIban, double amount) {
        runSafely("Transfer", () -> {
            if (fromAccount == null) throw new IllegalArgumentException("From account is null");
            if (toIban == null || toIban.isBlank()) throw new IllegalArgumentException("Target IBAN is empty");
            if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");

            Account toAccount = accountManager.findAccountByIban(toIban.trim());
            if (toAccount == null) {
                throw new IllegalArgumentException("Target account not found for IBAN: " + toIban);
            }

            LocalDate today = LocalDate.now();
            String transactor = (fromAccount.getOwner() != null && fromAccount.getOwner().getUserName() != null)
                    ? fromAccount.getOwner().getUserName()
                    : "GUI";

            transactionManager.executeTransfer(fromAccount, today, toAccount, amount, transactor);

            accountManager.storeBankAccounts();
            statementManager.storeStatements();
        });
    }

    @Override
    public void externalTransfer(Account fromAccount,
                                 String receiverIban,
                                 String code,
                                 String charges,
                                 double amount,
                                 String protocol) {
        runSafely("External transfer", () -> {
            if (fromAccount == null) throw new IllegalArgumentException("From account is null");
            if (receiverIban == null || receiverIban.isBlank()) throw new IllegalArgumentException("Receiver IBAN is empty");
            if (code == null || code.isBlank()) throw new IllegalArgumentException("BIC/SWIFT is empty");
            if (charges == null || charges.isBlank()) throw new IllegalArgumentException("Charges is empty");
            if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");
            if (protocol == null || protocol.isBlank()) throw new IllegalArgumentException("Protocol is empty");

            com.bank.transfer.ExternalTransfer bridge;
            String p = protocol.trim().toUpperCase();
            switch (p) {
                case "SEPA" -> bridge = new com.bank.transfer.BankTransfer(new com.bank.transfer.SepaProtocol());
                case "SWIFT" -> bridge = new com.bank.transfer.BankTransfer(new com.bank.transfer.SwiftProtocol());
                default -> throw new IllegalArgumentException("Unknown protocol: " + protocol);
            }

            LocalDate today = LocalDate.now();
            String transactor = (fromAccount.getOwner() != null && fromAccount.getOwner().getUserName() != null)
                    ? fromAccount.getOwner().getUserName()
                    : "GUI";

            transactionManager.executeExternalTransfer(
                    fromAccount,
                    today,
                    receiverIban.trim(),
                    code.trim(),
                    amount,
                    charges.trim(),
                    bridge,
                    transactor
            );

            accountManager.storeBankAccounts();
            statementManager.storeStatements();
        });
    }


    @Override
    public void payRfBill(Account fromAccount, String rfCode) {
        runSafely("Pay bill", () -> {
            if (fromAccount == null) {
                throw new IllegalArgumentException("Select an account first");
            }
            if (!(fromAccount instanceof PersonalAccount payer)) {
                throw new IllegalArgumentException("Bill payment requires a PersonalAccount payer");
            }
            if (rfCode == null || rfCode.isBlank()) {
                throw new IllegalArgumentException("RF/Bill code is empty");
            }

            String targetCode = rfCode.trim();

            // ===== Ownership check (equivalent to payer.whichOwner(i.getVatNumber())) =====
            // We don't have "Individual i" here, so we use the payer's primary owner VAT.
            String ownerVat = payer.getOwnerVat(); // or payer.getPrimaryOwnerVat() if you have it
            if (ownerVat == null || ownerVat.isBlank()) {
                throw new IllegalStateException("Cannot verify account ownership (missing owner VAT)");
            }
            if (!payer.whichOwner(ownerVat)) {
                throw new IllegalArgumentException("This account does not belong to you");
            }

            // ===== Find bill (CLI: findIssuedBill(billId)) =====
            // If rfCode is actually your bill number, use findIssuedBill.
            // If rfCode is the payment code, keep findIssuedBillByRfCode.
            Bill bill = billManager.findIssuedBill(targetCode);
            if (bill == null) {
                // fallback to RF/payment code search if you want both to work
                bill = findIssuedBillByRfCode(targetCode);
            }
            if (bill == null) {
                throw new IllegalArgumentException("Bill was not found or is already paid: " + targetCode);
            }

            // ===== Resolve receiver (CLI: findAccountByVat(bill.getIssuer())) =====
            Account receiverAcc = accountManager.findAccountByVat(bill.getIssuer());
            if (!(receiverAcc instanceof BusinessAccount receiver)) {
                throw new IllegalArgumentException("Receiver business account not found for issuer: " + bill.getIssuer());
            }

            LocalDate today = LocalDate.now();
            String transactor = payer.getIban(); // CLI uses iban as transactor

            billManager.payCustomerBill(bill, today, payer, receiver, transactor);

            // Persist
            accountManager.storeBankAccounts();
            billManager.storeIssuedBills();
            billManager.storePaidBills();
            statementManager.storeStatements();
        });
    }


    @Override
    public List<Bill> loadIssuedBills(User companyUser) {
        if (companyUser == null || !isCompany(companyUser)) return List.of();

        String companyVat = tryGetUserVat(companyUser);
        if (companyVat == null || companyVat.isBlank()) return List.of();

        List<Bill> result = new ArrayList<>();
        for (Bill b : billManager.getIssuedBills()) {
            String billCompanyVat = tryGetBillCompanyVat(b);
            if (billCompanyVat != null && billCompanyVat.equals(companyVat)) {
                result.add(b);
            }
        }
        return result;
    }

    @Override
    public List<Account> getAllAccounts() {
        return AccountManager.getInstance().getBankAccounts();
    }

    @Override
    public void simulateUntil(String date) {
        TimeSimulator.getInstance().simulateUntil(date);
    }

    @Override
    public String adminShowCustomers() {
        StringBuilder sb = new StringBuilder();

        sb.append("-- Individual Customers --\n");
        for (User u : UserManager.getInstance().getUsers()) {
            if (u instanceof Individual) sb.append(u).append("\n");
        }

        sb.append("\n-- Company Customers --\n");
        for (User u : UserManager.getInstance().getUsers()) {
            if (u instanceof Company) sb.append(u).append("\n");
        }

        return sb.toString().isBlank() ? "No customers found." : sb.toString();
    }

    @Override
    public String adminShowCustomerDetails(String vatNumber) {
        if (vatNumber == null || vatNumber.isBlank())
            throw new IllegalArgumentException("VAT number is empty");

        User u = UserManager.getInstance().findCustomerByVat(vatNumber.trim());
        if (u == null) return "Customer wasnt found.";
        return u.toString();
    }

    @Override
    public String adminShowBankAccounts() {
        var accounts = AccountManager.getInstance().getBankAccounts();
        if (accounts == null || accounts.isEmpty()) return "No bank accounts found.";

        StringBuilder sb = new StringBuilder();
        sb.append("-- Personal Accounts --\n");
        for (Account a : accounts) {
            if (a instanceof PersonalAccount) sb.append(a).append("\n");
        }

        sb.append("\n-- Business Accounts --\n");
        for (Account a : accounts) {
            if (a instanceof BusinessAccount) sb.append(a).append("\n");
        }

        return sb.toString();
    }

    @Override
    public String adminShowBankAccountInfo(String iban) {
        if (iban == null || iban.isBlank())
            throw new IllegalArgumentException("IBAN is empty");

        Account a = AccountManager.getInstance().findAccountByIban(iban.trim());
        if (a == null) return "Account wasnt found.";
        return a.toString();
    }

    @Override
    public String adminShowBankAccountStatements(String iban) {
        if (iban == null || iban.isBlank())
            throw new IllegalArgumentException("IBAN is empty");

        String i = iban.trim();

        Account acc = AccountManager.getInstance().authenticate(i);
        if (acc == null) return "Iban wasnt found.";

        StatementManager sm = StatementManager.getInstance();
        sm.loadStatementsFromFile(i);
        List<Statement> list = sm.getStatementsForIban(i);

        if (list == null || list.isEmpty()) return "No statements for iban: " + i;

        StringBuilder sb = new StringBuilder();
        for (Statement s : list) sb.append(s).append("\n");
        return sb.toString();
    }

    @Override
    public String adminShowIssuedCompanyBills() {
        var issued = BillManager.getInstance().getIssuedBills();
        if (issued == null || issued.isEmpty()) return "No issued bills found.";

        StringBuilder sb = new StringBuilder();
        for (Bill b : issued) sb.append(b).append("\n");
        return sb.toString();
    }

    @Override
    public String adminShowPaidCompanyBills() {
        var paid = BillManager.getInstance().getPaidBills();
        if (paid == null || paid.isEmpty()) return "No paid bills found.";

        StringBuilder sb = new StringBuilder();
        for (Bill b : paid) sb.append(b).append("\n");
        return sb.toString();
    }

    @Override
    public String adminLoadCompanyBills() {
        // CLI loads bills on date "2025-05-01"
        LocalDate d = LocalDate.parse("2025-05-01");
        BillManager.getInstance().loadBillsOnDate(d);

        // Return what exists now in issued list (most useful for GUI)
        var issued = BillManager.getInstance().getIssuedBills();
        return "Loaded bills for date: " + d + "\nIssued bills now: " + (issued == null ? 0 : issued.size());
    }

    @Override
    public String adminListStandingOrders() {
        var orders = StandingOrderManager.getInstance().getActiveOrders();
        if (orders == null || orders.isEmpty()) return "No active standing orders.";

        StringBuilder sb = new StringBuilder();
        for (StandingOrder o : orders) sb.append(o).append("\n");
        return sb.toString();
    }

    @Override
    public void adminPayCustomersBill(String customerIban, String billCode) {
        if (customerIban == null || customerIban.isBlank())
            throw new IllegalArgumentException("Customer IBAN is empty");
        if (billCode == null || billCode.isBlank())
            throw new IllegalArgumentException("Bill code is empty");

        String iban = customerIban.trim();
        String code = billCode.trim();

        Bill bill = BillManager.getInstance().findIssuedBill(code);
        if (bill == null) throw new IllegalArgumentException("Bill wasnt found.");

        Account payerAcc = AccountManager.getInstance().authenticate(iban);
        if (!(payerAcc instanceof PersonalAccount payer))
            throw new IllegalArgumentException("Customer wasnt found or IBAN is not PersonalAccount.");

        Account receiverAcc = AccountManager.getInstance().findAccountByVat(bill.getIssuer());
        if (!(receiverAcc instanceof BusinessAccount receiver))
            throw new IllegalArgumentException("Receiver wasnt found.");

        BillManager.getInstance().payCustomerBill(bill, LocalDate.now(), payer, receiver, "Admin");
    }

    @Override
    public String adminSimulateTimePassing(String dateUntil) {
        if (dateUntil == null || dateUntil.isBlank())
            throw new IllegalArgumentException("Date is empty");

        return captureConsoleOutput(() -> TimeSimulator.getInstance().simulateUntil(dateUntil.trim()));
    }

    // ===================== helpers =====================

    private Bill findIssuedBillByRfCode(String rfCode) {
        for (Bill b : billManager.getIssuedBills()) {
            if (b == null || b.getPaid()) continue;
            String code = safeTrim(b.getPaymentCode());
            if (code != null && code.equals(rfCode)) return b;
        }
        return null;
    }

    private BusinessAccount resolveBillReceiverBusinessAccount(Bill bill) {
        String receiverIban = tryInvokeString(bill,
                "getReceiverIban",
                "getCompanyIban",
                "getIssuerIban",
                "getPayeeIban"
        );
        if (receiverIban != null && !receiverIban.isBlank()) {
            Account acc = accountManager.findAccountByIban(receiverIban.trim());
            if (acc instanceof BusinessAccount ba) return ba;
        }

        String companyVat = tryGetBillCompanyVat(bill);
        if (companyVat != null && !companyVat.isBlank()) {
            Account acc = accountManager.findAccountByVat(companyVat.trim());
            if (acc instanceof BusinessAccount ba) return ba;
        }

        return null;
    }

    private String tryGetUserVat(User user) {
        String vat = tryInvokeString(user, "getVatNumber", "getVat", "getVatNumberCompany", "getVatNumberCustomer");
        if (vat != null) return vat;
        return null;
    }

    private String tryGetBillCompanyVat(Bill bill) {
        return bill.getIssuer();
    }

    private String tryGetBillCustomerVat(Bill bill) {
        return bill.getCustomer();
    }


    private String tryInvokeString(Object target, String... methodNames) {
        if (target == null) return null;
        for (String m : methodNames) {
            try {
                Method method = target.getClass().getMethod(m);
                Object val = method.invoke(target);
                if (val instanceof String s && !s.isBlank()) return s;
            } catch (Exception ignored) {
                // ignore and try next
            }
        }
        return null;
    }

    private String safeTrim(String s) {
        return s == null ? null : s.trim();
    }

    private String captureConsoleOutput(Runnable action) {
        java.io.PrintStream oldOut = System.out;
        java.io.PrintStream oldErr = System.err;

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        java.io.PrintStream ps = new java.io.PrintStream(baos);

        try {
            System.setOut(ps);
            System.setErr(ps);
            action.run();
        } finally {
            System.out.flush();
            System.err.flush();
            System.setOut(oldOut);
            System.setErr(oldErr);
        }

        // ensure persisted
        AccountManager.getInstance().storeBankAccounts();
        BillManager.getInstance().storeIssuedBills();
        BillManager.getInstance().storePaidBills();
        StatementManager.getInstance().storeStatements();
        StandingOrderManager.getInstance().saveActiveOrders();

        return baos.toString();
    }
}
