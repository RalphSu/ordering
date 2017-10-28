package net.samhouse.rabbitmq;

import net.samhouse.model.Step;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootConfiguration
@EnableRabbit
public class Sender {
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
     * @return
     */
    @Bean
    public Jackson2JsonMessageConverter senderMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }

    /**
     * @return
     */
    @Bean
    public DirectExchange orderExchange() {
        DirectExchange exchange = new DirectExchange("order");
        return exchange;
    }

    /**
     * @return
     */
    @Bean
    public Queue scheduleQueue() {
        return new Queue(Step.Phase.SCHEDULING.value());
    }

    /**
     * @return
     */
    @Bean
    public Binding scheduleBinding() {
        return BindingBuilder
                .bind(scheduleQueue())
                .to(orderExchange())
                .with(Step.Phase.SCHEDULING.value());
    }
}
