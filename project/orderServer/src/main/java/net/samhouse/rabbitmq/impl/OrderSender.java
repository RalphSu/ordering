package net.samhouse.rabbitmq.impl;

import net.samhouse.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class OrderSender {

    private static final Logger log = LoggerFactory.getLogger(OrderSender.class);

    private final RabbitTemplate rabbitTemplate;

    @Autowired
    public OrderSender(final RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    /**
     * @param order
     */
    public void deliverOrder(Order order) {
        log.info("Deliver order[{}, {}] to queue",
                order.getOrderID(), order.getCurrentStep().getCurrentPhase());
        try {
            rabbitTemplate.convertAndSend(order.getCurrentStep().getCurrentPhase().value(), order);
        } catch (AmqpException e) {
            log.error("Deliver order[{}] failed: {} {}", order.getOrderID(), e.getMessage(), e.getCause());
        }
    }
}
