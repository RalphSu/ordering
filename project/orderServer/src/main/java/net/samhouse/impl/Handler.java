package net.samhouse.impl;

import net.samhouse.model.Order;

/**
 *
 */
public interface Handler {
    /**
     *
      * @return
     */
    String getName();

    void handleOrder(Order order, String queue);
}
