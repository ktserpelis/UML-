package com.bank.ui.gui.views.models;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class BillsTableModel<Bill> extends AbstractTableModel {
    private final List<Bill> rows = new ArrayList<>();

    public void setRows(List<Bill> bills) {
        rows.clear();
        rows.addAll(bills);
        fireTableDataChanged();
    }

    @Override public int getRowCount() { return rows.size(); }
    @Override public int getColumnCount() { return 1; }
    @Override public String getColumnName(int c) { return "Bill"; }
    @Override public Object getValueAt(int r, int c) { return String.valueOf(rows.get(r)); }
}
