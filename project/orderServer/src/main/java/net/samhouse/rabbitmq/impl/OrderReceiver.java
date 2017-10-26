package net.samhouse.rabbitmq.impl;

import net.samhouse.rabbitmq.Handler;
import net.samhouse.model.Order;
import net.samhouse.model.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class OrderReceiver {

    private static final Logger log = LoggerFactory.getLogger(OrderReceiver.class);

    @Autowired
    private OrderSender orderSender;

    @Autowired
    private List<Handler> handlers;

    private Map<String, Handler> handlerMap;

    @PostConstruct
    void init() {
        handlerMap = handlers.stream()
                .collect(Collectors.toMap(Handler::getName, h -> h));

        // Using completed handler for failed as well
        // as both of them will go to db finally
        handlerMap.put(Step.FAILED, handlerMap.get(Step.COMPLETED));
    }

    /**
     * Leverage consumer threads to handling orders concurrently so far,
     * Of course if needed, we can have every handler a thread pool, and
     * in receiveOrder function, we put the order in a queue of each thread pool
     * @param order
     * @param queue
     */
    @RabbitListener(queues = {Step.SCHEDULING, Step.PRE_PROCESSING,
    Step.PROCESSING, Step.POST_PROCESSING, Step.COMPLETED, Step.FAILED})
    public void receiveOrder(final Order order, @Header(AmqpHeaders.CONSUMER_QUEUE) String queue) {

        log.debug("Receive order[{},{}] from queue[{}].",
                order.getOrderID(), order.getCurrentStep().getCurrentPhase(), queue);

        // find related handler
        Handler handler = handlerMap.get(queue);
        if (handler == null) {
            log.error("Can not find handler for order[{},{}] from queue[{}]",
                    order.getOrderID(), order.getCurrentStep().getCurrentPhase(), queue);
            return;
        }

        // dealing with order
        boolean passToNext = handler.handleOrder(order, queue);
        log.debug("Handling of order[{}, {}] is completed",
                order.getOrderID(), order.getCurrentStep().getCurrentPhase());

        // deliver order if needed
        if (passToNext) {
            orderSender.deliverOrder(order);
            log.debug("deliver order[{}] to queue[{}] finished",
                    order.getOrderID(), order.getCurrentStep().getCurrentPhase());
        }
        else {
            log.debug("Order[{}, {}] is in end state",
                    order.getOrderID(), order.getCurrentStep().getCurrentPhase());
        }
    }
}
