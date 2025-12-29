package com.bank.dao.bills;

import com.bank.dao.AbstractFsDAO;
import com.bank.model.bills.Bill;
import com.bank.storage.CSVStorageManager;
import com.bank.storage.StorableList;
import com.bank.storage.UnMarshalingException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.LocalDate;

public class FsDailyBillDAO extends AbstractFsDAO<Bill> implements DailyBillDAO {

    private static final String BILLS_DIR = "bills/";

    private final CSVStorageManager storage = CSVStorageManager.getInstance();

    public FsDailyBillDAO() {
        super(BILLS_DIR);
    }


    @Override
    public StorableList<Bill> loadBillsForDate(LocalDate date) {
        String fileName = BILLS_DIR + date.toString() + ".csv";
        StorableList<Bill> list = new StorableList<>();

        try {
            storage.loadObject(list, fileName);
        } catch (FileNotFoundException e) {
            System.out.println("[BillDAO] No bills file for date " + date + " (" + fileName + ")");
        } catch (IOException | UnMarshalingException e) {
            throw new RuntimeException("Error loading bills for date " + date + " from " + fileName, e);
        }

        return list;
    }

    @Override
    public void saveBillsForDate(LocalDate date, StorableList<Bill> bills) {
        String fileName = BILLS_DIR + date.toString() + ".csv";
        try {
            storage.storeObject(bills, fileName, false);
        } catch (IOException e) {
            throw new RuntimeException("Error saving bills for date " + date + " to " + fileName, e);
        }
    }
}
