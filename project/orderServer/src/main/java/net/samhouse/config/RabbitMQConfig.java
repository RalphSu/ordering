package net.samhouse.config;

import net.samhouse.model.Order;
import net.samhouse.impl.OrderListener;
import net.samhouse.model.Step;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Spring configuration class
 */
@SpringBootConfiguration
public class RabbitMQConfig {
    private static final String END = "END";

    @Value("${mq.rabbit.host}")
    private String host;

    @Value("${mq.rabbit.port}")
    private Integer port;

    @Value("${mq.rabbit.username}")
    private String username;

    @Value("${mq.rabbit.password}")
    private String password;

    @Value("${mq.rabbit.vhost}")
    private String vhost;

    /**
     *
     * @return Return caching connection factory bean
     */
    @Bean
    public ConnectionFactory connectionFactory() {
        CachingConnectionFactory connectionFactory =
                new CachingConnectionFactory(host, port);

        connectionFactory.setUsername(username);
        connectionFactory.setPassword(password);
        connectionFactory.setVirtualHost(vhost);

        return connectionFactory;
    }

    /**
     *
     * @param connectionFactory
     * @return return an AmqpAdmin bean using caching connection factory
     */
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
    }

    @Bean
    public RabbitMessagingTemplate rabbitTemplate(RabbitTemplate rabbitTemplate) {
        RabbitMessagingTemplate rabbitMessagingTemplate = new RabbitMessagingTemplate();
        rabbitMessagingTemplate.setMessageConverter(new MappingJackson2MessageConverter());
        rabbitMessagingTemplate.setRabbitTemplate(rabbitTemplate);
        return rabbitMessagingTemplate;
    }

    @Bean
    public Queue scheduleQueue() {
        return new Queue("order"+Step.Phase.SCHEDULING.value());
    }

//    @Bean
//    public Queue preprocessQueue() {
//        return new Queue("order"+Step.Phase.PRE_PROCESSING.value());
//    }
//
//    @Bean
//    public Queue processQueue() {
//        return new Queue("order"+Step.Phase.PROCESSING.value());
//    }
//
//    @Bean
//    public Queue postprocessQueue() {
//        return new Queue("order"+Step.Phase.POST_PROCESSING.value());
//    }
//
//    @Bean
//    public Queue endQueue() {
//        return new Queue("order"+END);
//    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory,
                                             OrderListener listener) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

//        Stream<String> phases = Stream.of(Step.Phase.values())
//                .map(s -> "order" + s.value());
//        container.setQueueNames(appendToStream(phases, "order"+END).toArray(String[]::new));

        container.setQueueNames(new String[] {"orderSCHEDULING"});
        container.setMessageListener(listener);

        return container;
    }

    private <T> Stream<T> appendToStream(Stream<? extends T> stream, T element) {
        return Stream.concat(stream, Stream.of(element));
    }

    @Bean
    OrderListener listener() {
        return new OrderListener();
    }

    @Bean
    DirectExchange exchange() {
        return new DirectExchange("orderExchange");
    }

    @Bean
    Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with("order");
    }
}
