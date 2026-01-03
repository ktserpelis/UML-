package com.bank.ui.gui.facade;

import com.bank.managers.AccountManager;
import com.bank.managers.BillManager;
import com.bank.managers.StatementManager;
import com.bank.managers.TransactionManager;
import com.bank.model.accounts.Account;
import com.bank.model.accounts.BusinessAccount;
import com.bank.model.accounts.PersonalAccount;
import com.bank.model.bills.Bill;
import com.bank.model.statements.Statement;
import com.bank.model.users.Company;
import com.bank.model.users.Customer;
import com.bank.model.users.User;

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
        // Outstanding bills = issuedBills that match this user (customer/company)
        var issued = billManager.getIssuedBills();
        if (issued == null || issued.isEmpty() || user == null) return List.of();

        String userVat = tryGetUserVat(user);
        if (userVat == null || userVat.isBlank()) return List.of();

        List<Bill> result = new ArrayList<>();

        if (isCompany(user)) {
            // bills issued BY the company
            for (Bill b : issued) {
                String companyVat = tryGetBillCompanyVat(b);
                if (companyVat != null && companyVat.equals(userVat) && !b.getPaid()) {
                    result.add(b);
                }
            }
        } else {
            // bills owed BY the customer
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
        if (account == null) throw new IllegalArgumentException("Account is null");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");

        LocalDate today = LocalDate.now();
        String transactor = (account.getOwner() != null && account.getOwner().getUserName() != null)
                ? account.getOwner().getUserName()
                : "GUI";

        transactionManager.executeDeposit(account, today, amount, transactor, "Deposit");

        // Persist effects (balances + statements)
        accountManager.storeBankAccounts();
        statementManager.storeStatements();
    }

    @Override
    public void withdraw(Account account, double amount) {
        if (account == null) throw new IllegalArgumentException("Account is null");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");

        LocalDate today = LocalDate.now();
        String transactor = (account.getOwner() != null && account.getOwner().getUserName() != null)
                ? account.getOwner().getUserName()
                : "GUI";

        transactionManager.executeWithdrawal(account, today, amount, transactor, "Withdrawal");

        accountManager.storeBankAccounts();
        statementManager.storeStatements();
    }

    @Override
    public void transfer(Account fromAccount, String toIban, double amount) {
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
    }

    @Override
    public void payRfBill(Account fromAccount, String rfCode) {
        if (fromAccount == null) throw new IllegalArgumentException("From account is null");
        if (!(fromAccount instanceof PersonalAccount payer)) {
            throw new IllegalArgumentException("Bill payment requires a PersonalAccount payer");
        }
        if (rfCode == null || rfCode.isBlank()) throw new IllegalArgumentException("RF code is empty");

        String targetCode = rfCode.trim();

        Bill bill = findIssuedBillByRfCode(targetCode);
        if (bill == null) {
            throw new IllegalArgumentException("No issued (unpaid) bill found for RF code: " + targetCode);
        }

        // Find receiver business account (best effort via reflection: receiver IBAN first, then company VAT)
        BusinessAccount receiver = resolveBillReceiverBusinessAccount(bill);
        if (receiver == null) {
            throw new IllegalStateException(
                    "Could not resolve receiver BusinessAccount for bill RF=" + targetCode +
                            ". Ensure Bill exposes receiver IBAN or company VAT."
            );
        }

        LocalDate today = LocalDate.now();
        String transactor = (payer.getOwner() != null && payer.getOwner().getUserName() != null)
                ? payer.getOwner().getUserName()
                : "GUI";

        billManager.payCustomerBill(bill, today, payer, receiver, transactor);

        accountManager.storeBankAccounts();
        billManager.storeIssuedBills();
        billManager.storePaidBills();
        statementManager.storeStatements();
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
        // 1) Try receiver IBAN getters
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

        // 2) Try company VAT getters
        String companyVat = tryGetBillCompanyVat(bill);
        if (companyVat != null && !companyVat.isBlank()) {
            Account acc = accountManager.findAccountByVat(companyVat.trim());
            if (acc instanceof BusinessAccount ba) return ba;
        }

        return null;
    }

    private String tryGetUserVat(User user) {
        // Customer/Company usually have vatNumber; try common method names
        String vat = tryInvokeString(user, "getVatNumber", "getVat", "getVatNumberCompany", "getVatNumberCustomer");
        if (vat != null) return vat;

        // fallback: if your User.type stores VAT (unlikely) - no safe fallback otherwise
        return null;
    }

    private String tryGetBillCompanyVat(Bill bill) {
        return tryInvokeString(bill,
                "getCompanyVat",
                "getCompanyVatNumber",
                "getIssuerVat",
                "getIssuerVatNumber",
                "getPayeeVat",
                "getPayeeVatNumber"
        );
    }

    private String tryGetBillCustomerVat(Bill bill) {
        return tryInvokeString(bill,
                "getCustomerVat",
                "getCustomerVatNumber",
                "getPayerVat",
                "getPayerVatNumber"
        );
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
}
