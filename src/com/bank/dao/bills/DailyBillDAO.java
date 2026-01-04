package com.bank.dao.bills;

import com.bank.model.bills.Bill;
import com.bank.storage.StorableList;

import java.time.LocalDate;

public interface DailyBillDAO {

    // Ημερήσιοι λογαριασμοί για συγκεκριμένη ημερομηνία (bills/YYYY-MM-DD.csv)
    StorableList<Bill> loadBillsForDate(LocalDate date);
    void saveBillsForDate(LocalDate date, StorableList<Bill> bills);


}
