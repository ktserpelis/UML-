package com.bank.dao.bills;

import com.bank.dao.AbstractFsDAO;
import com.bank.model.bills.Bill;
import com.bank.storage.StorableList;
import java.io.IOException;

public class FsPaidBillDAO extends AbstractFsDAO<Bill> implements PaidBillDAO {

    private static final String PAID_FILE = "bills/paid.csv";

    public FsPaidBillDAO() {
        super(PAID_FILE);
    }

    @Override
    public StorableList<Bill> loadPaidBills() {
        return loadList();
    }

    @Override
    public void savePaidBills(StorableList<Bill> paidBills, boolean append) {
        try {
            storage.storeObject(paidBills, PAID_FILE, false);
        } catch (IOException e) {
            throw new RuntimeException("DAO save failed for file: " + PAID_FILE, e);
        }
    }
}
