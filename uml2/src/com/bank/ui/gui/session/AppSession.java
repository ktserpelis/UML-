package com.bank.ui.gui.session;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;

public class AppSession<User,Account,Transaction,Bill> {
    public static final String EVT_USERS = "users";
    public static final String EVT_ACCOUNTS = "accounts";
    public static final String EVT_TRANSACTIONS = "transactions";
    public static final String EVT_BILLS = "bills";
    public static final String EVT_CURRENT_USER = "currentUser";
    public static final String EVT_SELECTED_ACCOUNT = "selectedAccount";

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);

    private final List<User> users = new ArrayList<>();
    private final List<Account> accounts = new ArrayList<>();
    private final List<Transaction> transactions = new ArrayList<>();
    private final List<Bill> bills = new ArrayList<>();

    private User currentUser;
    private Account selectedAccount;

    public void addListener(PropertyChangeListener l) { pcs.addPropertyChangeListener(l); }
    public void removeListener(PropertyChangeListener l) { pcs.removePropertyChangeListener(l); }

    public List<User> getUsers() { return List.copyOf(users); }
    public List<Account> getAccounts() { return List.copyOf(accounts); }
    public List<Transaction> getTransactions() { return List.copyOf(transactions); }
    public List<Bill> getBills() { return List.copyOf(bills); }

    public User getCurrentUser() { return currentUser; }
    public Account getSelectedAccount() { return selectedAccount; }

    public void setUsers(List<User> newUsers) {
        List<User> old = List.copyOf(users);
        users.clear(); users.addAll(newUsers);
        pcs.firePropertyChange(EVT_USERS, old, List.copyOf(users));
    }

    public void setAccounts(List<Account> newAccounts) {
        List<Account> old = List.copyOf(accounts);
        accounts.clear(); accounts.addAll(newAccounts);
        pcs.firePropertyChange(EVT_ACCOUNTS, old, List.copyOf(accounts));
    }

    public void setTransactions(List<Transaction> newTxs) {
        List<Transaction> old = List.copyOf(transactions);
        transactions.clear(); transactions.addAll(newTxs);
        pcs.firePropertyChange(EVT_TRANSACTIONS, old, List.copyOf(transactions));
    }

    public void setBills(List<Bill> newBills) {
        List<Bill> old = List.copyOf(bills);
        bills.clear(); bills.addAll(newBills);
        pcs.firePropertyChange(EVT_BILLS, old, List.copyOf(bills));
    }

    public void setCurrentUser(User user) {
        User old = this.currentUser;
        this.currentUser = user;
        pcs.firePropertyChange(EVT_CURRENT_USER, old, user);
    }

    public void setSelectedAccount(Account account) {
        Account old = this.selectedAccount;
        this.selectedAccount = account;
        pcs.firePropertyChange(EVT_SELECTED_ACCOUNT, old, account);
    }

    public void clearUserScopedData() {
        setAccounts(List.of());
        setTransactions(List.of());
        setBills(List.of());
        setSelectedAccount(null);
    }
}
