import net.samhouse.config.RabbitMQConfig;
import net.samhouse.model.Order;
import net.samhouse.model.Step;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.amqp.core.AmqpAdmin;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.util.AssertionErrors.assertEquals;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = RabbitMQConfig.class)
@TestPropertySource(locations = "classpath:application.properties")
public class MessageTest {

    @Autowired
    private AmqpAdmin amqpAdmin;

    @Autowired
    private RabbitMessagingTemplate rabbitMessagingTemplate;

    @Test
    public void testMessageSendAndConsume() {
        rabbitMessagingTemplate.convertAndSend(Step.Phase.SCHEDULING.value(), new Order());

        Order order = rabbitMessagingTemplate.receiveAndConvert(Order.class);
        assertNotNull("", order.getOrderID());
    }
}
