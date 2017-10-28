package net.samhouse.model;

import net.samhouse.db.config.OrderDBConfig;
import net.samhouse.db.service.OrderService;
import net.samhouse.db.service.StepService;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = {OrderDBConfig.class})
@Transactional
public class DatabaseTest {

    private String orderID = "orderID";

    @Autowired
    private StepService stepService;

    @Autowired
    private OrderService orderService;

    @Before
    public void setUp() {
        orderService.delete(orderID);
    }

    @After
    public void tearDown() {
        orderService.delete(orderID);
    }

    @Test
    public void testGetStepService() {

        // insert function of step service is do nothing
        Order order = new Order(orderID, 0, 0,
                (new Step()).init(), "");
        try {
            stepService.insert(new Step());
        } catch (UnsupportedOperationException e) {
            assertTrue("insert function of step service is not implemented", true);
        }

        try {
            stepService.getById(orderID);
        } catch (EmptyResultDataAccessException e) {
            assertTrue("Get an empty step from db should throw exception", true);
        }

        orderService.insert(order);
        Step newStep = stepService.findCurrentStep(orderID);
        assertEquals(newStep.getCurrentPhase(), order.getCurrentStep().getCurrentPhase());
    }

    @Test
    public void testGetOrderService() {
        Order order = new Order(orderID, 0, 0, (new Step()).init(), "");
        orderService.insert(order);

        Order newOrder = orderService.getById(order.getOrderID());

        assertEquals(order, newOrder);
        assertEquals(orderID, order.getOrderID());
    }
}
