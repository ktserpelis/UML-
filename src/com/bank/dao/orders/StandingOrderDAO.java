package com.bank.dao.orders;

import com.bank.model.orders.StandingOrder;
import com.bank.storage.StorableList;

public interface StandingOrderDAO {
    StorableList<StandingOrder> findAll();
    void saveAll(StorableList<StandingOrder> orders);
}
