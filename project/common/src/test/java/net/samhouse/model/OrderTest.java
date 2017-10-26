package net.samhouse.model;

import org.junit.Test;

import static org.junit.Assert.*;

public class OrderTest {

    @Test
    public void testNewOrder() {
        Order order = new Order();
        assertEquals("A new order by Order() should be in scheduling phase",
                Step.Phase.SCHEDULING, order.getCurrentStep().getCurrentPhase());
        assertNotNull("Order id should not be null", order.getOrderID());

        Order newOne = new Order("orderID", System.currentTimeMillis(),
                System.currentTimeMillis(), new Step());
        assertEquals("A new order by Order(...) should be in scheduling phase",
                Step.Phase.SCHEDULING, newOne.getCurrentStep().getCurrentPhase());
        assertEquals("order id should equal to 'orderID'",
                "orderID", newOne.getOrderID());
    }

    @Test
    public void testChangeStep() {
        Order order = new Order();
        long currentTime = System.currentTimeMillis();
        assertEquals("Step list size should be 0 in the original status",
                0, order.getSteps().size());

        Step processing = new Step(Step.Phase.PROCESSING, currentTime, currentTime);
        order.changeCurrentStep(processing);
        assertEquals("After set current step a new step, step list should be 1",
                1, order.getSteps().size());

        //logical equal
        assertTrue("The new phase of step should be in processing",
                processing.getCurrentPhase().equals(order.getCurrentStep().getCurrentPhase()));
        assertTrue("set current step to a different phase should return a new object",
                processing != order.getCurrentStep());

        order.moveToNextStep();
        assertEquals("After move current step a new phase, step list should be 2",
                2, order.getSteps().size());
        assertTrue("The new phase of step should be in post processing",
                Step.Phase.POST_PROCESSING.equals(order.getCurrentStep().getCurrentPhase()));

        // this is a new java object, but a same order from logic
        assertTrue("Move current step to next phase should return a new object",
                processing != order.getCurrentStep());
    }
}
