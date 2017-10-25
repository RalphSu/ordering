package net.samhouse.impl;

import net.samhouse.model.Order;
import net.samhouse.model.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Random;

public class ScheduleHandler implements Handler {

    private static Logger log = LoggerFactory.getLogger(ScheduleHandler.class);

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
            log.error("ScheduleHandler failed to deal with order[{}] with exception{}", order.getOrderID(), e);
        }
    }

    @Override
    public String getName() {
        return Step.SCHEDULING;
    }
}
