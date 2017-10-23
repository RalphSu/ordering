package net.samhouse.config;

import net.samhouse.impl.OrderHandler;
import net.samhouse.model.Step;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer;
import org.springframework.amqp.rabbit.listener.adapter.MessageListenerAdapter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;

import java.util.stream.Stream;

/**
 * Spring configuration class
 */
@SpringBootConfiguration
public class RabbitMQConfig {

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

    @Value("${mq.rabbit.consumers}")
    private Integer consumers;

    @Value("${mq.rabbit.maxconsumers}")
    private Integer maxconsumers;

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
        rabbitMessagingTemplate.setMessageConverter(messageConverter());
        rabbitMessagingTemplate.setRabbitTemplate(rabbitTemplate);
        return rabbitMessagingTemplate;
    }

    @Bean
    public MappingJackson2MessageConverter messageConverter() {
        return new MappingJackson2MessageConverter();
    }

    @Bean
    public DirectExchange orderExchange() {
        DirectExchange exchange = new DirectExchange("order");
        return exchange;
    }

    @Bean
    public Queue scheduleQueue() {
        return new Queue(Step.Phase.SCHEDULING.value());
    }

    @Bean
    public Binding scheduleBinding() {
        return BindingBuilder
                .bind(scheduleQueue())
                .to(orderExchange())
                .with(Step.Phase.SCHEDULING.value());
    }

    @Bean
    public Queue preprocessQueue() {
        return new Queue(Step.Phase.PRE_PROCESSING.value());
    }

    @Bean
    public Binding preprocessBind() {
        return BindingBuilder
                .bind(preprocessQueue())
                .to(orderExchange())
                .with(Step.Phase.PRE_PROCESSING.value());
    }

    @Bean
    public Queue processQueue() {
        return new Queue(Step.Phase.PROCESSING.value());
    }

    @Bean
    public Binding processBind() {
        return BindingBuilder
                .bind(processQueue())
                .to(orderExchange())
                .with(Step.Phase.PROCESSING.value());
    }

    @Bean
    public Queue postprocessQueue() {
        return new Queue(Step.Phase.POST_PROCESSING.value());
    }

    @Bean
    public Binding postprocessBind() {
        return BindingBuilder
                .bind(postprocessQueue())
                .to(orderExchange())
                .with(Step.Phase.POST_PROCESSING.value());
    }

    @Bean
    SimpleMessageListenerContainer container(ConnectionFactory connectionFactory) {
        SimpleMessageListenerContainer container = new SimpleMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);

        Stream<String> phases = Stream.of(Step.Phase.SCHEDULING,
                Step.Phase.PRE_PROCESSING, Step.Phase.PROCESSING, Step.Phase.POST_PROCESSING)
                .map(Step.Phase::value);
        container.setQueueNames(phases.toArray(String[]::new));

        container.setMessageListener(new MessageListenerAdapter(handler()));
        container.setConcurrentConsumers(consumers);
        container.setMaxConcurrentConsumers(maxconsumers);

        return container;
    }

    @Bean
    OrderHandler handler() {
        return new OrderHandler();
    }
}
