package net.samhouse;

import net.samhouse.model.Order;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderHandlerTest {
    @Autowired
    RabbitTemplate rabbitTemplate;

}
