package com.bank.managers;

import com.bank.dao.factory.DAOFactory;
import com.bank.dao.orders.StandingOrderDAO;
import com.bank.model.orders.PaymentOrder;
import com.bank.model.orders.StandingOrder;
import com.bank.model.orders.TransferOrder;
import com.bank.model.accounts.Account;
import com.bank.model.accounts.PersonalAccount;
import com.bank.model.accounts.BusinessAccount;
import com.bank.model.bills.Bill;
import com.bank.storage.StorableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StandingOrderManager {

    private static StandingOrderManager instance;

    private final StandingOrderDAO standingOrderDAO;
    private final StorableList<StandingOrder> activeOrders;

    private StandingOrderManager() {
        DAOFactory factory = DAOFactory.getDAOFactory(DAOFactory.FS);
        this.standingOrderDAO = factory.getStandingOrderDAO();
        this.activeOrders = standingOrderDAO.findAll();
    }

    public static StandingOrderManager getInstance() {
        if (instance == null) {
            instance = new StandingOrderManager();
        }
        return instance;
    }

    // ================== Βασικά CRUD ==================

    public void saveActiveOrders() {
        standingOrderDAO.saveAll(activeOrders);
    }

    public void addStandingOrder(StandingOrder order) {
        for (StandingOrder o : activeOrders) {
            if (o.getOrderId().equals(order.getOrderId())) {
                System.out.println("Standing order with id " + order.getOrderId() + " already exists.");
                return;
            }
        }
        activeOrders.add(order);
        saveActiveOrders();
    }

    public boolean removeStandingOrder(String orderId) {
        StandingOrder toRemove = findById(orderId);
        if (toRemove == null) {
            return false;
        }
        activeOrders.remove(toRemove);
        saveActiveOrders();
        return true;
    }

    public StandingOrder findById(String orderId) {
        for (StandingOrder o : activeOrders) {
            if (o.getOrderId().equals(orderId)) {
                return o;
            }
        }
        return null;
    }

    public List<StandingOrder> findByCustomer(String customerId) {
        List<StandingOrder> result = new ArrayList<>();
        for (StandingOrder o : activeOrders) {
            if (o.getCustomer().equals(customerId)) {
                result.add(o);
            }
        }
        return result;
    }

    public StorableList<StandingOrder> getActiveOrders() {
        return activeOrders;
    }

    public void showActiveOrders() {
        if (activeOrders.isEmpty()) {
            System.out.println("No active standing orders.");
            return;
        }
        for (StandingOrder o : activeOrders) {
            System.out.println(o);
        }
    }


    // ================== Εκτέλεση για συγκεκριμένη ημερομηνία ==================

    public void executeOrdersFor(LocalDate date) {
        // Αντιγράφουμε τη λίστα για να αποφύγουμε ConcurrentModificationException
        List<StandingOrder> snapshot = new ArrayList<>(activeOrders);

        for (StandingOrder order : snapshot) {

            if (!order.isActive()) {
                continue;
            }

            if (!shouldRunOnDate(order, date)) {
                continue;
            }

            boolean success = false;

            if (order instanceof TransferOrder) {
                // → χρήση shouldRunOnDate
                if (!shouldRunOnDate(order, date)) {
                    continue; // δεν είναι η μέρα του transfer
                }
                success = executeTransferOrder((TransferOrder) order, date);

            } else if (order instanceof PaymentOrder) {
                // → ΔΙΚΟΣ τους έλεγχος με dueDate
                success = executePaymentOrder((PaymentOrder) order, date);
            }

            if (!success) {
                handleFailure(order);
            } else {
                handleSuccess(order, date);
            }
        }

        // Αποθήκευση αλλαγών (active/failedAttempts κλπ)
        saveActiveOrders();
    }

    // ------------------- Βοηθητικές μέθοδοι -------------------

    private boolean shouldRunOnDate(StandingOrder order, LocalDate date) {

        // 1. Έλεγχος ημερομηνιών ισχύος
        if (order.getStartDate() != null && date.isBefore(order.getStartDate())) {
            return false;
        }

        if (order.getEndDate() != null && date.isAfter(order.getEndDate())) {
            return false;
        }

        // 2. Αν είναι TransferOrder → ελέγχουμε περιοδικότητα
        if (order instanceof TransferOrder) {

            TransferOrder tOrder = (TransferOrder) order;

            // πρέπει να ταιριάζει η μέρα του μήνα
            if (date.getDayOfMonth() != tOrder.getDayOfMonth()) {
                return false;
            }

            // υπολογισμός πόσοι μήνες έχουν περάσει από startDate
            LocalDate start = order.getStartDate();

            int monthsBetween =
                    (date.getYear() - start.getYear()) * 12
                            + (date.getMonthValue() - start.getMonthValue());

            // μην ξεκινήσουμε πριν το startDate
            if (monthsBetween < 0) {
                return false;
            }

            // τρέχουμε μόνο κάθε N μήνες
            if (monthsBetween % tOrder.getFrequencyInMonths() != 0) {
                return false;
            }
        }

        // 3. Αν δεν είναι TransferOrder (δηλαδή PaymentOrder):
        //     Αν βρισκόμαστε εντός start/end → απλά επιτρέπεται να τρέξει
        return true;
    }


    private boolean executeTransferOrder(TransferOrder order, LocalDate date) {
        Account sender = AccountManager.getInstance().findAccountByIban(order.getChargeAccountIban());
        Account receiver = AccountManager.getInstance().findAccountByIban(order.getCreditAccount());

        if (sender == null || receiver == null) {
            System.out.println("[StandingOrder] Missing account for transfer order " + order.getOrderId());
            return false;
        }

        double amount = order.getAmount();
        if (sender.getBalance() < amount) {
            System.out.println("[StandingOrder] Insufficient balance for transfer order " + order.getOrderId());
            return false;
        }

        if (order.getProtocol() != null) {
            TransactionManager.getInstance().executeTransferWithProtocol(sender, date, receiver, amount, "StandingOrder", order.getProtocol());
        } else {
            TransactionManager.getInstance().executeTransfer(sender,date,receiver, amount, "StandingOrder");
        }
        return true;
    }

    private boolean executePaymentOrder(PaymentOrder order, LocalDate date) {
        // Βρίσκουμε τον λογαριασμό από το paymentCode
        Bill bill =  BillManager.getInstance().findIssuedBillByPaymentCodeAndDueDate(order.getPaymentCode(), date);
        if (bill == null) {
            System.out.println("[StandingOrder] No issued bill for paymentCode " + order.getPaymentCode());
            return true;
        }

        if (bill.getAmount() > order.getMaxAmount()) {
            System.out.println("[StandingOrder] Bill amount exceeds maxAmount in order " + order.getOrderId());
            return false;
        }

        Account payerAcc = AccountManager.getInstance().findAccountByIban(order.getChargeAccountIban());
        if (!(payerAcc instanceof PersonalAccount)) {
            System.out.println("[StandingOrder] Charge account is not PersonalAccount for order " + order.getOrderId());
            return false;
        }

        // issuer account (εταιρικός)
        BusinessAccount receiverAcc = (BusinessAccount) AccountManager.getInstance().findAccountByVat(bill.getIssuer());  // ή ό,τι μηχανισμό έχεις για να βρεις το business account
        if (receiverAcc == null) {
            System.out.println("[StandingOrder] Could not find business account for issuer " + bill.getIssuer());
            return false;
        }

        PersonalAccount payer = (PersonalAccount) payerAcc;

        if (payer.getBalance() < bill.getAmount()) {
            System.out.println("[StandingOrder] Insufficient balance for payment order " + order.getOrderId());
            return false;
        }

        BillManager.getInstance().payCustomerBill(bill, date, payer, receiverAcc, "StandingOrder");
        return true;
    }

    private void handleFailure(StandingOrder order) {
        order.setFailedAttempts(order.getFailedAttempts() + 1);

        if (order.getFailedAttempts() >= order.getMaxAttempts()) {
            order.setActive(false);
            System.out.println("[StandingOrder] Order " + order.getOrderId() + " deactivated after failures.");
            // Αν η εκφώνηση θέλει extra fee, μπορείς να το χρεώσεις εδώ
        }
    }

    private void handleSuccess(StandingOrder order, LocalDate date) {
        order.setFailedAttempts(0);
        // Αν θες να κρατάς lastExecutionDate:
        // order.setLastExecutionDate(date);
    }
}
