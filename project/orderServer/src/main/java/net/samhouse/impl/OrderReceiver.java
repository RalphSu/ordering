package net.samhouse.impl;

import net.samhouse.model.Order;
import net.samhouse.model.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

@Service
public class OrderReceiver {

    private static final Logger log = LoggerFactory.getLogger(OrderReceiver.class);

    private OrderDistributor orderDistributor;

    @Autowired
    public void setOrderDistributor(OrderDistributor orderDistributor) {
        this.orderDistributor = orderDistributor;
    }

    @RabbitListener(queues = {Step.SCHEDULING, Step.PRE_PROCESSING,
    Step.PROCESSING, Step.POST_PROCESSING, Step.COMPLETED, Step.FAILED})
    public void receiveOrder(final Order order, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {
        log.debug("Receive order[{}] from queue[{}].", order.getOrderID(), queue);

        orderDistributor.handleOrder(order, queue);
    }
}
