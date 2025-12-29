package com.bank.managers;

import com.bank.dao.factory.DAOFactory;
import com.bank.dao.bills.DailyBillDAO;
import com.bank.dao.bills.IssuedBillDAO;
import com.bank.dao.bills.PaidBillDAO;
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
        // Παίρνουμε τα DAO από τη Factory (Abstract Factory pattern)
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.FS);

        this.dailyBillDAO  = factory.getDailyBillDAO();
        this.issuedBillDAO = factory.getIssuedBillDAO();
        this.paidBillDAO   = factory.getPaidBillDAO();

        // Φορτώνουμε issued & paid από τα αντίστοιχα csv
        this.issuedBills = issuedBillDAO.loadIssuedBills();
        this.paidBills   = paidBillDAO.loadPaidBills();

        // Η λίστα με τα ημερήσια bills ξεκινάει κενή
        this.bills = new StorableList<>();
    }

    public static BillManager getInstance() {
        if (instance == null) {
            instance = new BillManager();
        }
        return instance;
    }

    // =====================================================================
    // Φόρτωση / αποθήκευση bills για συγκεκριμένη ημερομηνία
    // =====================================================================

    /**
     * Φορτώνει τους λογαριασμούς από το αρχείο bills/YYYY-MM-DD.csv
     * και ενημερώνει τη λίστα issuedBills με όσους δεν είναι ήδη issued / paid.
     */
    public void loadBillsOnDate(LocalDate date) {
        bills.clear();
        bills.addAll(dailyBillDAO.loadBillsForDate(date));

        System.out.println(bills.size() + " bills loaded from: bills/" + date + ".csv");

        for (Bill bill : bills) {
            System.out.println(bill);
        }

        // Προσθήκη νέων issued bills
        for (Bill bill : bills) {
            if (!bill.getPaid() && !alreadyIssued(bill)) {
                issuedBills.add(bill);
            }
        }

        // Αποθήκευση ενημερωμένου issued.csv
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



    // Issued Bills
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

        // Κανονικοποίηση του κωδικού που ψάχνουμε
        String targetCode = paymentCode.trim();

        //System.out.println("[DEBUG] Searching bill: code=" + targetCode + ", dueDate=" + dueDate);

        for (Bill bill : issuedBills) {
            String billCode = bill.getPaymentCode();
            LocalDate billDue = bill.getDueDate();
            boolean paid = bill.getPaid();

            // Προστασία από null + trim
            if (billCode != null) {
                billCode = billCode.trim();
            }

//            System.out.println("[DEBUG] candidate: code=" + billCode
//                    + ", dueDate=" + billDue
//                    + ", paid=" + paid);

            boolean codeMatches = (billCode != null && billCode.equals(targetCode));
            boolean dateMatches = (billDue != null && billDue.equals(dueDate));
            boolean notPaid = !paid;

//            // Βάλε και αυτό για να δούμε ακριβώς τι αποτυγχάνει
//            System.out.println("        [DEBUG] cmp -> codeMatches=" + codeMatches
//                    + ", dateMatches=" + dateMatches
//                    + ", notPaid=" + notPaid);

            if (codeMatches && dateMatches && notPaid) {
                System.out.println("[DEBUG] --> MATCH");
                return bill;
            }
        }

        System.out.println("[DEBUG] --> NO MATCH");
        return null;
    }



//    public Bill findIssuedBillByPaymentCodeAndDueDate(String paymentCode ,LocalDate dueDate) {
//        for (Bill bill : issuedBills) {
//            if (paymentCode.equals(bill.getPaymentCode())
//                    && dueDate.equals(bill.getDueDate())
//                    && !bill.getPaid()) {
//                return bill;
//            }
//        }
//        return null;
//    }


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


    //Paid Bills
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

    // =====================================================================
    // Πληρωμή λογαριασμού
    // =====================================================================

    /**
     * Πληρώνει έναν issued λογαριασμό:
     * - ελέγχει υπόλοιπο
     * - καλεί TransactionManager.executeBills(...)
     * - αλλάζει την κατάσταση του Bill σε paid
     * - τον μετακινεί από issued -> paid
     * - αποθηκεύει τα αντίστοιχα csv (issued.csv, paid.csv)
     */

    public void payCustomerBill(Bill bill,LocalDate date,PersonalAccount payer , BusinessAccount receiver,String transactor) {
        if (payer.getBalance()<bill.getAmount()) {
            System.out.println("Insufficient balance to pay bill");
            return;
        }

        // Πραγματική πληρωμή + statements
        TransactionManager.getInstance().executeBills(bill,date,payer,receiver,transactor);

        // Αν η executeBills δεν αλλάζει το flag, φρόντισε να το κάνεις εδώ:
        bill.setPaid(true);

        paidBills.add(bill);
        removeIssuedBill(bill);

        // Αποθήκευση αλλαγών σε csv
        storeIssuedBills();
        storePaidBills();

        System.out.println("Bill " + bill.getBillNumber() + " was paid.");
    }

    // =====================================================================
    // Getters (χρήσιμοι για CLI/GUI)
    // =====================================================================

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