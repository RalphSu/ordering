package net.samhouse.impl;

import net.samhouse.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

/**
 *
 */
public class OrderHandler {
    @Autowired
    RabbitTemplate rabbitTemplate;

    public void handleMessage(Order order) {
        System.out.println("" + order.getOrderID());

        switch (order.getCurrentStep().getCurrentPhase()) {
            case SCHEDULING:
                break;
            case PRE_PROCESSING:
                break;
            case PROCESSING:
                break;
            case POST_PROCESSING:
                break;
            case COMPLETED:
            case FAILED:
                break;
            default:
                break;
        }
    }
}
