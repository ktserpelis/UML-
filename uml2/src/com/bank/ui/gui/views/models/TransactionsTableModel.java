package com.bank.ui.gui.views.models;

import javax.swing.table.AbstractTableModel;

import com.bank.model.transactions.Transaction;

import java.util.ArrayList;
import java.util.List;

public class TransactionsTableModel<Transaction> extends AbstractTableModel {
    private final List<Transaction> rows = new ArrayList<>();

    public void setRows(List<Transaction> txs) {
        rows.clear();
        rows.addAll(txs);
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return 1; }
    @Override public String getColumnName(int c) { return "Transaction"; }
    @Override public Object getValueAt(int r, int c) { return String.valueOf(rows.get(r)); }
}
