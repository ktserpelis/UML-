package com.bank.managers;

import java.time.LocalDate;

public class TimeSimulator {
    private static TimeSimulator instance;

    public static TimeSimulator getInstance() {
        if (instance == null) {
            instance = new TimeSimulator();
        }
        return instance;
    }

    public void simulateUntil(String dateGiven) {
        LocalDate dateUntil = LocalDate.parse(dateGiven);
        //LocalDate todayDate = LocalDate.now();
        LocalDate todayDate = LocalDate.parse("2025-05-01");

        while (todayDate.isBefore(dateUntil) || todayDate.isEqual(dateUntil)) {
            System.out.println(todayDate);

            executeDailyOperations(todayDate);

            todayDate = todayDate.plusDays(1);
        }
        System.out.println("Simulation finished!");
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

        //System.out.println("Charging maintenance fees for business accounts...");

        System.out.println("Executing standing orders...");
        StandingOrderManager.getInstance().executeOrdersFor(todayDate);

    }
}