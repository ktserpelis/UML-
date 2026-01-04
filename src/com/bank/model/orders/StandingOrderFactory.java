package com.bank.model.orders;

import java.time.LocalDate;

public final class StandingOrderFactory {

    private StandingOrderFactory() {}

    public static TransferOrder createTransferOrder(
            String customerVat,
            String title,
            String description,
            String chargeIban,
            String creditIban,
            double amount,
            LocalDate start,
            LocalDate end,
            double fee,
            int frequencyInMonths,
            int dayOfMonth
    ) {
        String id = "TO" + System.currentTimeMillis();

        TransferOrder o = new TransferOrder(
                id,
                title,
                description,
                customerVat,
                start,
                end,
                fee,
                chargeIban.trim(),
                amount,
                creditIban.trim(),
                frequencyInMonths,
                dayOfMonth
        );

        o.type = "TransferOrder"; // keep CSV compatibility
        return o;
    }

    public static PaymentOrder createPaymentOrder(
            String customerVat,
            String title,
            String description,
            String chargeIban,
            String paymentCode,
            double maxAmount,
            LocalDate start,
            LocalDate end,
            double fee
    ) {
        String id = "PO" + System.currentTimeMillis();

        PaymentOrder o = new PaymentOrder(
                id,
                title,
                description,
                customerVat,
                start,
                end,
                fee,
                chargeIban.trim(),
                paymentCode.trim(),
                maxAmount
        );

        o.type = "PaymentOrder";
        return o;
    }
}
