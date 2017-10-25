package net.samhouse.impl;

import net.samhouse.model.Order;
import net.samhouse.model.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class PreProcessHandler implements Handler {

    private static Logger log = LoggerFactory.getLogger(PreProcessHandler.class);

    @Override
    public String getName() {
        return Step.PRE_PROCESSING;
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
            log.error("Preprocess failed to deal with order[{}] with exception{}", order.getOrderID(), e);
        }
    }
}
