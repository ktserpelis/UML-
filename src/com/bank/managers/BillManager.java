package com.bank.managers;

import com.bank.dao.factory.DAOFactory;
import com.bank.dao.bills.DailyBillDAO;
import com.bank.dao.bills.IssuedBillDAO;
import com.bank.dao.bills.PaidBillDAO;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.model.accounts.BusinessAccount;
import com.bank.model.accounts.PersonalAccount;
import com.bank.model.bills.Bill;
import com.bank.storage.StorableList;
import java.time.LocalDate;

public class BillManager {
    private static BillManager instance;

    // --- DAO objects ---
    private final DailyBillDAO dailyBillDAO;
    private final IssuedBillDAO issuedBillDAO;
    private final PaidBillDAO paidBillDAO;

    // --- In–memory lists ---
    private final StorableList<Bill> bills;       // bills της τρέχουσας ημερομηνίας
    private final StorableList<Bill> issuedBills; // ενεργοί / προς πληρωμή
    private final StorableList<Bill> paidBills;   // πληρωμένοι λογαριασμοί

    private BillManager() {
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.FS);

        this.dailyBillDAO  = factory.getDailyBillDAO();
        this.issuedBillDAO = factory.getIssuedBillDAO();
        this.paidBillDAO   = factory.getPaidBillDAO();

        this.issuedBills = issuedBillDAO.loadIssuedBills();
        this.paidBills   = paidBillDAO.loadPaidBills();

        this.bills = new StorableList<>();
    }

    public static BillManager getInstance() {
        if (instance == null) {
            instance = new BillManager();
        }
        return instance;
    }

    public Bill createCompanyBill(String type,
                                  String paymentCode,
                                  String billNumber,
                                  String issuerVat,
                                  String customerVat,
                                  double amount,
                                  LocalDate issueDate,
                                  LocalDate dueDate) {

        if (issuerVat == null || issuerVat.isBlank()) throw new IllegalArgumentException("Issuer VAT is empty");
        if (customerVat == null || customerVat.isBlank()) throw new IllegalArgumentException("Customer VAT is empty");
        if (amount <= 0) throw new IllegalArgumentException("Amount must be > 0");
        if (issueDate == null) throw new IllegalArgumentException("Issue date is empty");
        if (dueDate == null) throw new IllegalArgumentException("Due date is empty");
        if (dueDate.isBefore(issueDate)) throw new IllegalArgumentException("Due date cannot be before issue date");

        // generate missing codes if not provided
        String finalPaymentCode = (paymentCode == null || paymentCode.isBlank())
                ? ("RF" + System.currentTimeMillis())
                : paymentCode.trim();

        String finalBillNumber = (billNumber == null || billNumber.isBlank())
                ? ("B" + issueDate.toString().replace("-", "") + "-" + System.currentTimeMillis())
                : billNumber.trim();

        String finalType = (type == null || type.isBlank()) ? "Bill" : type.trim();

        Bill newBill = new Bill(finalType, finalPaymentCode, finalBillNumber,
                issuerVat.trim(), customerVat.trim(), amount, issueDate, dueDate);

        // ✅ Add to daily bills file for issueDate (so simulator can load it on that date)
        StorableList<Bill> daily = dailyBillDAO.loadBillsForDate(issueDate);
        daily.add(newBill);
        dailyBillDAO.saveBillsForDate(issueDate, daily);

        // ✅ Add to issued bills now (so GUI company sees it immediately)
        if (!alreadyIssued(newBill)) {
            issuedBills.add(newBill);
            storeIssuedBills();
        }

        return newBill;
    }


    public void loadBillsOnDate(LocalDate date) {
        bills.clear();
        bills.addAll(dailyBillDAO.loadBillsForDate(date));

        System.out.println(bills.size() + " bills loaded from: bills/" + date + ".csv");

        for (Bill bill : bills) {
            System.out.println(bill);
        }

        for (Bill bill : bills) {
            if (!bill.getPaid() && !alreadyIssued(bill)) {
                issuedBills.add(bill);
            }
        }

        storeIssuedBills();
    }

    private boolean alreadyIssued(Bill bill) {
        for (Bill b : issuedBills) {
            if (b.getBillNumber().equals(bill.getBillNumber())) {
                return true;
            }
        }
        return false;
    }

    public void storeBillsOnDate(LocalDate date) {
        dailyBillDAO.saveBillsForDate(date, bills);
    }

    public void storeIssuedBills() {
        issuedBillDAO.saveIssuedBills(issuedBills);
    }

    public void showIssuedBills() {
        if (issuedBills.isEmpty()) {
            System.out.println("No issued bills found.");
        } else {
            for (Bill bill : issuedBills) {
                System.out.println(bill);
            }
        }
    }

    public Bill findIssuedBillByPaymentCodeAndDueDate(String paymentCode, LocalDate dueDate) {
        if (paymentCode == null || dueDate == null) {
            return null;
        }

        String targetCode = paymentCode.trim();

        for (Bill bill : issuedBills) {
            String billCode = bill.getPaymentCode();
            LocalDate billDue = bill.getDueDate();
            boolean paid = bill.getPaid();

            if (billCode != null) {
                billCode = billCode.trim();
            }

            boolean codeMatches = (billCode != null && billCode.equals(targetCode));
            boolean dateMatches = (billDue != null && billDue.equals(dueDate));
            boolean notPaid = !paid;

            if (codeMatches && dateMatches && notPaid) {
                System.out.println("[DEBUG] --> MATCH");
                return bill;
            }
        }

        System.out.println("[DEBUG] --> NO MATCH");
        return null;
    }

    public Bill findIssuedBill(String billnum){
        for(Bill bill : issuedBills){
            if(billnum.equals(bill.getBillNumber())) {
                return bill;
            }
        }
        return null;
    }

    public void removeIssuedBill(Bill bill) {
        issuedBills.remove(bill);
    }

    public void storePaidBills() {
        paidBillDAO.savePaidBills(paidBills, false);
    }

    public void showPaidBills() {
        if (paidBills.isEmpty()) {
            System.out.println("No paid bills found.");
        } else {
            for (Bill bill : paidBills) {
                System.out.println(bill);
            }
        }
    }

    public void payCustomerBill(Bill bill, LocalDate date, PersonalAccount payer,
                                BusinessAccount receiver, String transactor) {

        // ✅ ONLY CHANGE: throw exception instead of printing+returning
        if (payer.getBalance() < bill.getAmount()) {
            throw new InsufficientFundsException("Insufficient balance to pay bill");
        }

        TransactionManager.getInstance().executeBills(bill, date, payer, receiver, transactor);

        bill.setPaid(true);

        paidBills.add(bill);
        removeIssuedBill(bill);

        storeIssuedBills();
        storePaidBills();

        System.out.println("Bill " + bill.getBillNumber() + " was paid.");
    }

    public StorableList<Bill> getBills() {
        return bills;
    }

    public StorableList<Bill> getIssuedBills() {
        return issuedBills;
    }

    public StorableList<Bill> getPaidBills() {
        return paidBills;
    }
}
