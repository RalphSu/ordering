package net.samhouse.db.dao;

import net.samhouse.model.Order;

/**
 *
 */
public interface OrderDao {
    /**
     * @param order
     */
    void insert(Order order);

    /**
     * @param orderID
     * @return
     */
    Order findOrderById(String orderID);
}
