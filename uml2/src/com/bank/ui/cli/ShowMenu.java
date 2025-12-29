package com.bank.ui.cli;

import com.bank.model.users.User;

import java.time.LocalDate;

public interface ShowMenu {
    void showMenu(User user, LocalDate date);
}
