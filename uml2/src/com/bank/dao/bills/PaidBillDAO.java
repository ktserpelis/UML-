package com.bank.dao.bills;

import com.bank.model.bills.Bill;
import com.bank.storage.StorableList;

public interface PaidBillDAO {
    // Paid bills (bills/paid.csv)
    StorableList<Bill> loadPaidBills();
    void savePaidBills(StorableList<Bill> paidBills, boolean append);
}
