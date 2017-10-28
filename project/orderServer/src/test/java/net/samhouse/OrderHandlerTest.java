package net.samhouse;

import net.samhouse.model.Order;
import net.samhouse.model.Step;
import net.samhouse.rabbitmq.impl.handlers.ProcessHandler;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OrderHandlerTest {
    @Test
    public void testProcessHandler() {
        ProcessHandler processHandler = new ProcessHandler();
        Order order = new Order("orderID", 0, 0,
                new Step(Step.Phase.PROCESSING, 0, 0), "");

        assertEquals(Step.PROCESSING, processHandler.getName());
        assertTrue(processHandler.handleOrder(order, Step.PROCESSING));

        assertEquals(Step.Phase.POST_PROCESSING, order.getCurrentStep().getCurrentPhase());
    }
}
