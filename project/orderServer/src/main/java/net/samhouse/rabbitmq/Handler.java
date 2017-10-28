package net.samhouse.rabbitmq;

import net.samhouse.model.Order;

/**
 *
 */
public interface Handler {

    /**
     * @return
     */
    String getName();

    /**
     * @param order
     * @param queue
     * @return true means to pass order on handler link, otherwise stop
     */
    boolean handleOrder(Order order, String queue);
}
