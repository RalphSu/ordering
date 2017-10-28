package net.samhouse;

import net.samhouse.config.OrderServiceConfig;
import net.samhouse.model.Order;
import net.samhouse.rabbitmq.impl.OrderSender;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = OrderServiceConfig.class)
public class MessageTest {

    @Autowired
    private OrderSender orderSender;

    @Test
    public void testSender() {
        Order order = new Order().init();
        orderSender.deliverOrder(order);
    }
}
