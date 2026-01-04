package com.bank.ui.gui.commands;

import com.bank.ui.gui.errors.ErrorBus;
import com.bank.ui.gui.facade.BankFacade;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class SimulateUntilDateCommand<User,Account,Transaction,Bill> implements Command {

    private final BankFacade<User,Account,Transaction,Bill> facade;
    private final String dateText;

    public SimulateUntilDateCommand(
            BankFacade<User,Account,Transaction,Bill> facade,
            String dateText) {
        this.facade = facade;
        this.dateText = dateText;
    }

    @Override
    public void execute() {
        try {
            if (dateText == null || dateText.isBlank()) {
                throw new IllegalArgumentException("Date is empty");
            }

            // ✅ validation only — backend does the simulation
            LocalDate.parse(dateText.trim()); // yyyy-MM-dd

            facade.adminSimulateTimePassing(dateText.trim());

        } catch (DateTimeParseException e) {
            ErrorBus.getInstance().publish(
                    "Invalid date",
                    "Use format yyyy-MM-dd (e.g. 2026-01-15)"
            );
        } catch (IllegalArgumentException e) {
            ErrorBus.getInstance().publish("Invalid input", e.getMessage());
        }
    }
}