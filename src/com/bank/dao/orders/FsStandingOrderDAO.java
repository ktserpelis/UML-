package com.bank.dao.orders;

import com.bank.dao.AbstractFsDAO;
import com.bank.model.orders.StandingOrder;
import com.bank.storage.StorableList;

public class FsStandingOrderDAO extends AbstractFsDAO<StandingOrder> implements StandingOrderDAO {

    private static final String ORDERS_FILE = "orders/active.csv";

    public FsStandingOrderDAO() {
        super(ORDERS_FILE);
    }

    @Override
    public StorableList<StandingOrder> findAll() {
        return loadList();
    }

    @Override
    public void saveAll(StorableList<StandingOrder> orders) {
        saveList(orders);
    }
}
