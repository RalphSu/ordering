package net.samhouse.rabbitmq.impl.handlers;

import net.samhouse.db.service.OrderService;
import net.samhouse.model.Order;
import net.samhouse.model.Step;
import net.samhouse.rabbitmq.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * order handler which will put completed and failed orders to db
 */
public class EndStateHandler implements Handler {

    private static Logger log = LoggerFactory.getLogger(EndStateHandler.class);

    @Autowired
    OrderService orderService;

    /**
     * Using "Completed" handler for both completed and failed queue
     *
     * @return
     */
    @Override
    public String getName() {
        return Step.COMPLETED;
    }

    @Override
    public boolean handleOrder(Order order, String queue) {
        try {
            log.info("Order[{}] in step[{}] will goto db",
                    order.getOrderID(), order.getCurrentStep().getCurrentPhase());
            orderService.insert(order);

        } catch (Exception e) {
            log.error("EndStateHandler failed to deal with order[{}] with exception {}", order.getOrderID(), e);
        }

        return false;
    }
}
