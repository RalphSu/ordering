package net.samhouse.rabbitmq.impl.handlers;

import net.samhouse.model.Order;
import net.samhouse.model.Step;
import net.samhouse.rabbitmq.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ThreadLocalRandom;

/**
 * pre processing order queue
 */
public class PreProcessHandler implements Handler {

    private static Logger log = LoggerFactory.getLogger(PreProcessHandler.class);

    @Override
    public String getName() {
        return Step.PRE_PROCESSING;
    }

    @Override
    public boolean handleOrder(Order order, String queue) {
        try {
            Thread.sleep(5000);
            ThreadLocalRandom threadLocalRandom = ThreadLocalRandom.current();
            if (threadLocalRandom.nextInt(100) < 5) {
                order.setToFailed();
            } else {
                order.moveToNextStep();
            }
        } catch (Exception e) {
            log.error("PreProcessHandler failed to deal with order[{}] with exception {}", order.getOrderID(), e);
        }

        return true;
    }
}
