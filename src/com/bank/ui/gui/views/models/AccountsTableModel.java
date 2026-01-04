package com.bank.ui.gui.views.models;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class AccountsTableModel<Account> extends AbstractTableModel {
    private final List<Account> rows = new ArrayList<>();

    public void setRows(List<Account> accounts) {
        rows.clear();
        rows.addAll(accounts);
        fireTableDataChanged();
    }

    public Account getAt(int row) { return rows.get(row); }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return 1; }
    @Override public String getColumnName(int c) { return "Account"; }
    @Override public Object getValueAt(int r, int c) { return String.valueOf(rows.get(r)); }
}

