package net.samhouse.config;

import net.samhouse.impl.*;
import net.samhouse.model.Step;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.annotation.RabbitListenerConfigurer;
import org.springframework.amqp.rabbit.config.SimpleRabbitListenerContainerFactory;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.listener.RabbitListenerEndpointRegistrar;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * Spring configuration class
 */
@SpringBootConfiguration
@EnableRabbit
public class OpsConfig implements RabbitListenerConfigurer{

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
     * consumer threads used by spring to receive messages
     */
    @Value("${mq.rabbit.consumers:5}")
    private Integer consumers;

    /**
     * max consumer threads
     */
    @Value("${mq.rabbit.maxconsumers:10}")
    private Integer maxconsumers;

    /**
     * return handler that really handle orders
     * @return
     */
    @Bean
    public List<Handler> handlers() {
        List<Handler> handlers = new ArrayList<>();
        handlers.add(new ScheduleHandler());
        handlers.add(new PreProcessHandler());
        handlers.add(new ProcessHandler());
        handlers.add(new PostProcessHandler());
        handlers.add(new EndStateHandler());
        return handlers;
    }

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
     * @return
     */
    @Bean
    public RabbitTemplate rabbitTemplate() {
        RabbitTemplate rabbitTemplate = new RabbitTemplate();
        rabbitTemplate.setConnectionFactory(connectionFactory());
        rabbitTemplate.setMessageConverter(senderMessageConverter());
        return rabbitTemplate;
    }

    /**
     *
     * @return
     */
    @Bean
    public MappingJackson2MessageConverter listenerMessageConverter() {
        return new MappingJackson2MessageConverter();
    }

    /**
     *
     * @return
     */
    @Bean
    public Jackson2JsonMessageConverter senderMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     *
     * @return
     */
    @Bean
    public DirectExchange orderExchange() {
        DirectExchange exchange = new DirectExchange("order");
        return exchange;
    }

    /**
     *
     * @return
     */
    @Bean
    public Queue scheduleQueue() {
        return new Queue(Step.Phase.SCHEDULING.value());
    }

    /**
     *
     * @return
     */
    @Bean
    public Binding scheduleBinding() {
        return BindingBuilder
                .bind(scheduleQueue())
                .to(orderExchange())
                .with(Step.Phase.SCHEDULING);
    }

    /**
     *
     * @return
     */
    @Bean
    public Queue preprocessQueue() {
        return new Queue(Step.Phase.PRE_PROCESSING.value());
    }

    /**
     *
     * @return
     */
    @Bean
    public Binding preprocessBind() {
        return BindingBuilder
                .bind(preprocessQueue())
                .to(orderExchange())
                .with(Step.Phase.PRE_PROCESSING);
    }

    /**
     *
     * @return
     */
    @Bean
    public Queue processQueue() {
        return new Queue(Step.Phase.PROCESSING.value());
    }

    /**
     *
     * @return
     */
    @Bean
    public Binding processBind() {
        return BindingBuilder
                .bind(processQueue())
                .to(orderExchange())
                .with(Step.Phase.PROCESSING);
    }

    /**
     *
     * @return
     */
    @Bean
    public Queue postprocessQueue() {
        return new Queue(Step.Phase.POST_PROCESSING.value());
    }

    /**
     *
     * @return
     */
    @Bean
    public Binding postprocessBind() {
        return BindingBuilder
                .bind(postprocessQueue())
                .to(orderExchange())
                .with(Step.Phase.POST_PROCESSING);
    }

    /**
     *
     * @return
     */
    @Bean
    public Queue completeQueue() {
        return new Queue(Step.Phase.COMPLETED.value());
    }

    /**
     *
     * @return
     */
    @Bean
    public Binding completeBind() {
        return BindingBuilder
                .bind(completeQueue())
                .to(orderExchange())
                .with(Step.Phase.COMPLETED);
    }

    /**
     *
     * @return
     */
    @Bean
    public Queue failedQueue() {
        return new Queue(Step.Phase.FAILED.value());
    }

    /**
     *
     * @return
     */
    @Bean
    public Binding failedBind() {
        return BindingBuilder
                .bind(failedQueue())
                .to(orderExchange())
                .with(Step.Phase.FAILED);
    }

    /**
     *
     * @return
     */
    @Bean
    public DefaultMessageHandlerMethodFactory messageHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();
        factory.setMessageConverter(listenerMessageConverter());
        return factory;
    }

    /**
     *
     * @return
     */
    @Bean
    public SimpleRabbitListenerContainerFactory rabbitListenerContainerFactory() {
        SimpleRabbitListenerContainerFactory factory = new SimpleRabbitListenerContainerFactory();
        factory.setConnectionFactory(connectionFactory());
        factory.setConcurrentConsumers(consumers);
        factory.setMaxConcurrentConsumers(maxconsumers);
        return factory;
    }

    /**
     *
     * @param registrar
     */
    @Override
    public void configureRabbitListeners(RabbitListenerEndpointRegistrar registrar) {
        registrar.setMessageHandlerMethodFactory(messageHandlerMethodFactory());
    }
}
