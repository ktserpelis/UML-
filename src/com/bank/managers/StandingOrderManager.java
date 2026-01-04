package com.bank.managers;

import com.bank.commands.Command;
import com.bank.commands.CommandExecutor;
import com.bank.commands.PayBillCommand;
import com.bank.commands.TransferCommand;
import com.bank.dao.factory.DAOFactory;
import com.bank.dao.orders.StandingOrderDAO;
import com.bank.model.accounts.Account;
import com.bank.model.accounts.BusinessAccount;
import com.bank.model.accounts.PersonalAccount;
import com.bank.model.bills.Bill;
import com.bank.model.orders.PaymentOrder;
import com.bank.model.orders.StandingOrder;
import com.bank.model.orders.TransferOrder;
import com.bank.storage.StorableList;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class StandingOrderManager {

    private static StandingOrderManager instance;
    private final StandingOrderDAO standingOrderDAO;
    private final StorableList<StandingOrder> activeOrders;
    private enum RunResult { SUCCESS, FAIL, SKIP }

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
        if (toRemove == null) return false;
        activeOrders.remove(toRemove);
        saveActiveOrders();
        return true;
    }

    public StandingOrder findById(String orderId) {
        for (StandingOrder o : activeOrders) {
            if (o.getOrderId().equals(orderId)) return o;
        }
        return null;
    }

    public StorableList<StandingOrder> getActiveOrders() {
        return activeOrders;
    }

    // ================== Εκτέλεση μέσω Commands ==================

    public void executeOrdersFor(LocalDate date) {
        List<StandingOrder> snapshot = new ArrayList<>(activeOrders);

        for (StandingOrder order : snapshot) {
            if (!order.isActive()) continue;
            if (!shouldRunOnDate(order, date)) continue;

            RunResult result = RunResult.SKIP;

            if (order instanceof TransferOrder t) {
                result = executeTransferOrder(t, date) ? RunResult.SUCCESS : RunResult.FAIL;
            } else if (order instanceof PaymentOrder p) {
                result = executePaymentOrder(p, date);
            }

            if (result == RunResult.SKIP) {
                continue; // don't count as failure
            } else if (result == RunResult.FAIL) {
                handleFailure(order);
            } else { // SUCCESS
                handleSuccess(order);

                // ✅ REMOVE ORDER AFTER SUCCESS
                activeOrders.remove(order);
            }
        }

        saveActiveOrders();
    }


    private boolean executeTransferOrder(TransferOrder order, LocalDate date) {
        Account sender = AccountManager.getInstance().findAccountByIban(order.getChargeAccountIban());
        Account receiver = AccountManager.getInstance().findAccountByIban(order.getCreditAccount());

        if (sender == null || receiver == null || sender.getBalance() < order.getAmount()) {
            return false;
        }

        try {
            // Χρήση του Command Pattern
            Command transferCmd = new TransferCommand(sender, date, receiver, order.getAmount(), "Auto-StandingOrder: " + order.getOrderId());
            CommandExecutor.getInstance().executeCommand(transferCmd);
            return true;
        } catch (Exception e) {
            System.err.println("Standing Order Transfer failed: " + e.getMessage());
            return false;
        }
    }

    private RunResult executePaymentOrder(PaymentOrder order, LocalDate date) {
        Bill bill = BillManager.getInstance()
                .findIssuedBillByPaymentCodeAndDueDate(order.getPaymentCode(), date);

        // ✅ bill doesn't exist today -> SKIP (not failure)
        if (bill == null) return RunResult.SKIP;

        if (bill.getAmount() > order.getMaxAmount()) return RunResult.FAIL;

        Account payerAny = AccountManager.getInstance().findAccountByIban(order.getChargeAccountIban());
        if (!(payerAny instanceof PersonalAccount payer)) return RunResult.FAIL;

        Account receiverAny = AccountManager.getInstance().findAccountByVat(bill.getIssuer());
        if (!(receiverAny instanceof BusinessAccount receiver)) return RunResult.FAIL;

        if (payer.getBalance() < bill.getAmount()) return RunResult.FAIL;

        try {
            Command payCmd = new PayBillCommand(
                    bill, date, payer, receiver, "Auto-StandingOrder: " + order.getOrderId()
            );
            CommandExecutor.getInstance().executeCommand(payCmd);
            return RunResult.SUCCESS;
        } catch (Exception e) {
            System.err.println("Standing Order Payment failed: " + e.getMessage());
            return RunResult.FAIL;
        }
    }



    private boolean shouldRunOnDate(StandingOrder order, LocalDate date) {
        if (order.getStartDate() != null && date.isBefore(order.getStartDate())) return false;
        if (order.getEndDate() != null && date.isAfter(order.getEndDate())) return false;

        // ✅ PaymentOrders run daily (bill may be due on any day)
        if (order instanceof PaymentOrder) {
            return true;
        }

        // ✅ TransferOrders use schedule
        if (order.getStartDate() == null) return false;

        if (order instanceof TransferOrder tOrder) {
            if (date.getDayOfMonth() != tOrder.getDayOfMonth()) return false;

            int monthsBetween =
                    (date.getYear() - order.getStartDate().getYear()) * 12
                            + (date.getMonthValue() - order.getStartDate().getMonthValue());

            return monthsBetween >= 0 && (monthsBetween % tOrder.getFrequencyInMonths() == 0);
        }

        return true;
    }


    private void handleSuccess(StandingOrder order) {
        order.setFailedAttempts(0);
    }

    private void handleFailure(StandingOrder order) {
        order.setFailedAttempts(order.getFailedAttempts() + 1);
        if (order.getFailedAttempts() >= order.getMaxAttempts()) {
            order.setActive(false);
        }
    }
}