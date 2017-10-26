package net.samhouse.db.service;

import net.samhouse.model.Order;

/**
 *
 */
public interface OrderService {
    /**
     * @param order
     */
    void insert(Order order);

    /**
     * @param orderID
     */
    void getOrderById(String orderID);
}
