package net.samhouse.impl;

import net.samhouse.model.Order;
import net.samhouse.model.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 *
 */
public class EndStateHandler implements Handler {

    private static Logger log = LoggerFactory.getLogger(EndStateHandler.class);

    /**
     * Using "Completed" handler for both completed and failed queue
     * @return
     */
    @Override
    public String getName() {
        return Step.COMPLETED;
    }

    @Override
    public void handleOrder(Order order, String queue) {
        try {
            Thread.sleep(5000);
            Random random = new Random(100);
            if (random.nextInt() <= 5) {
                order.setToFailed();
            } else {
                order.moveToNextStep();
            }
        } catch (Exception e) {
            log.error("EndStateHandler failed to deal with order[{}] with exception{}", order.getOrderID(), e);
        }
    }
}
