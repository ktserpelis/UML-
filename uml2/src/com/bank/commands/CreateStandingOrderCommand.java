package com.bank.commands;

import com.bank.managers.StandingOrderManager;
import com.bank.model.orders.StandingOrder;

public class CreateStandingOrderCommand implements Command {
    private StandingOrder order;

    public CreateStandingOrderCommand(StandingOrder order) {
        this.order = order;
    }

    @Override
    public void execute() throws Exception {
        StandingOrderManager.getInstance().addStandingOrder(order);
    }
}