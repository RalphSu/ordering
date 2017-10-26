package net.samhouse.rabbitmq.impl.handlers;

import net.samhouse.model.Order;
import net.samhouse.model.Step;
import net.samhouse.rabbitmq.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.concurrent.ThreadLocalRandom;

/**
 * post-process order handler
 */
public class PostProcessHandler implements Handler {

    private static Logger log = LoggerFactory.getLogger(PostProcessHandler.class);

    @Override
    public String getName() {
        return Step.POST_PROCESSING;
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
            log.error("PostProcessHandler failed to deal with order[{}] with exception {}", order.getOrderID(), e);
        }

        return true;
    }
}
