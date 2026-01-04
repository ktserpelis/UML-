package com.bank.dao.bills;

import com.bank.dao.AbstractFsDAO;
import com.bank.model.bills.Bill;
import com.bank.storage.StorableList;

public class FsIssuedBillDAO extends AbstractFsDAO<Bill> implements IssuedBillDAO {

    private static final String ISSUED_FILE = "bills/issued.csv";

    public FsIssuedBillDAO() {
        super(ISSUED_FILE);
    }

    @Override
    public StorableList<Bill> loadIssuedBills() {
        return loadList();
    }

    @Override
    public void saveIssuedBills(StorableList<Bill> issuedBills) {
        saveList(issuedBills);
    }
}
