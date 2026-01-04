package com.bank.dao.bills;

import com.bank.model.bills.Bill;
import com.bank.storage.StorableList;

public interface IssuedBillDAO {
    // Issued bills (bills/issued.csv)
    StorableList<Bill> loadIssuedBills();
    void saveIssuedBills(StorableList<Bill> issuedBills);

}
