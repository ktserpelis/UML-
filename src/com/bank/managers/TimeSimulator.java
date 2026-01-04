package com.bank.managers;

import java.time.LocalDate;

public class TimeSimulator {
    private static TimeSimulator instance;

    private static final LocalDate DEFAULT_START = LocalDate.parse("2025-12-01");

    // next date to execute
    private LocalDate currentDate = DEFAULT_START;

    private TimeSimulator() {}

    public static TimeSimulator getInstance() {
        if (instance == null) {
            instance = new TimeSimulator();
        }
        return instance;
    }

    /** Next date that will be executed on the next simulate run. */
    public LocalDate getCurrentDate() {
        return currentDate;
    }

    /** Reset simulator back to the default start (useful for testing). */
    public void reset() {
        currentDate = DEFAULT_START;
    }

    /** Reset simulator to a specific date. */
    public void resetTo(String date) {
        currentDate = LocalDate.parse(date.trim());
    }

    /**
     * Simulate from currentDate up to dateGiven (inclusive).
     * After finishing, currentDate becomes (dateGiven + 1 day).
     */
    public void simulateUntil(String dateGiven) {
        LocalDate dateUntil = LocalDate.parse(dateGiven.trim());

        // ✅ show start/target
        System.out.println("[Simulator] Starting from " + currentDate + " until " + dateUntil);

        if (currentDate.isAfter(dateUntil)) {
            System.out.println("[Simulator] Nothing to do. Current date " + currentDate +
                    " is after target " + dateUntil);
            return;
        }

        while (!currentDate.isAfter(dateUntil)) {
            System.out.println(currentDate);

            executeDailyOperations(currentDate);

            currentDate = currentDate.plusDays(1);
        }

        // ✅ show where it will resume next time
        System.out.println("[Simulator] Simulation finished! Next start date: " + currentDate);
    }

    private void executeDailyOperations(LocalDate todayDate) {
        BillManager.getInstance().loadBillsOnDate(todayDate);

        System.out.println("Calculating interests for accounts...");
        if (todayDate.getDayOfMonth() == todayDate.lengthOfMonth()) {
            try {
                System.out.println("Applying monthly interest and fees...");
                AccountManager.getInstance().calculateInterest(todayDate);
            } catch (Exception e) {
                System.out.println("Error applying interest/fees: " + e.getMessage());
            }
        }

        System.out.println("Executing standing orders...");
        StandingOrderManager.getInstance().executeOrdersFor(todayDate);
    }
}
