package net.samhouse;

import net.samhouse.model.Order;
import net.samhouse.model.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.annotation.EnableRabbit;
import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitMessagingTemplate;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.Banner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.env.CommandLinePropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthorizationInterceptor;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static java.lang.System.exit;

/**
 * A load generator program to insert Order into rabbitmq
 * do not use @SpringBootApplication as the 'common' package has db related beans
 * they will messed up this
 * TODO Add option for select different queue
 * TODO Add option for concurrently send message
 * TODO Add option for send to a specific queue
 * ...
 */
@EnableRabbit
@SpringBootConfiguration
public class LoadGenerator implements CommandLineRunner {

    private static Logger log = LoggerFactory.getLogger(LoadGenerator.class);

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

    @Value("${load.interval:1}")
    private Integer interval;

    @Value("${load.count:1000}")
    private Integer count;

    @Value("${rest.url}")
    private String resturl;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private RestTemplateBuilder restTemplateBuilder;


    public static void main(String[] args) throws Exception {

        SpringApplication app = new SpringApplication(LoadGenerator.class);
        app.setBannerMode(Banner.Mode.OFF);
        app.run(args);
    }

    /**
     * Real body for generator load
     *
     * @param args
     * @throws Exception
     */
    @Override
    public void run(String... args) throws Exception {
        String testType = "interval";
        String testMethod = "rabbit";

        // get the command line parameters
        CommandLinePropertySource clp = new SimpleCommandLinePropertySource(args);
        if (clp.containsProperty("type")) {
            testType = clp.getProperty("type");
        }
        if (clp.containsProperty("method")) {
            testMethod = clp.getProperty("method");
        }

        if (testMethod.equals("rabbit") || testMethod.equals("r")) {
            testRabbit(testType);
        } else if (testMethod.equals("rest") || testMethod.equals("t")) {
            testRest(testType);
        } else {
            System.out.println("-----------------------------------------------------------------------------");
            System.out.println("java -jar loadGenerator.jar --type=[interval|full] --method=[rabbit|rest]");
            System.out.println("Or you can use java -jar loadGenerator.jar --type=[i|f] --method=[r|t]");
            System.out.println("-----------------------------------------------------------------------------");
        }

        exit(0);
    }

    /**
     * main test function for sending message to rabbit
     *
     * @param testType
     * @throws InterruptedException
     */
    private void testRabbit(String testType) throws InterruptedException {
        if (testType.equals("interval") || testType.equals("i")) {
            CountDownLatch countDown = new CountDownLatch(count);

            final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(() -> {
                sendRabbit(countDown.getCount());
                countDown.countDown();
            }, 0, interval, TimeUnit.SECONDS);

            countDown.await();
        } else if (testType.equals("full") || testType.equals("f")) {
            // for loop to send message at a full speed
            for (int i = 0; i < count; i++) {
                sendRabbit(i);
            }
        }
    }

    /**
     * @param seq
     */
    private void sendRabbit(long seq) {
        Order order = (new Order()).init();
        order.setPayLoad("Count down meessage:" + Long.toString(seq));
        rabbitTemplate.convertAndSend(Step.SCHEDULING, order);
        log.info("Order {} sending completed!", order.getOrderID());
    }

    /**
     * main function to send rest request
     *
     * @param testType
     * @throws InterruptedException
     */
    private void testRest(String testType) throws InterruptedException {
        restTemplate.getInterceptors().add(
                new BasicAuthorizationInterceptor("submitter", "submitter"));

        if (testType.equals("interval") || testType.equals("i")) {
            CountDownLatch countDown = new CountDownLatch(count);

            final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(() -> {
                sendRest(countDown.getCount());
                countDown.countDown();
            }, 0, interval, TimeUnit.SECONDS);

            countDown.await();
        } else if (testType.equals("full") || testType.equals("f")) {

            // for loop to send message at a full speed
            for (int i = 0; i < count; i++) {
                sendRest(i);
            }
        }
    }

    /**
     * @param seq
     * @return
     */
    private int sendRest(long seq) {
        Order order = new Order();
        order.setPayLoad("Count down meessage:" + Long.toString(seq));
        HttpEntity<Order> httpEntity = new HttpEntity<>(order);
        ResponseEntity<String> responseEntity = restTemplate
                .exchange(resturl, HttpMethod.POST, httpEntity, String.class);

        String response = responseEntity.getBody();
        int status = responseEntity.getStatusCodeValue();

        log.info("Send post request {} completed with response[{}] [{}]", order.getPayLoad(), response, status);
        return status;
    }

    /**
     * Used for rest client
     *
     * @param builder
     * @return
     */
    @Bean
    public RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }

    /**
     * @return
     */
    @Bean
    public RestTemplateBuilder restTemplateBuilder() {
        return new RestTemplateBuilder();
    }

    /**
     * All the following beans are used for rabbitmq
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
     * @param connectionFactory
     * @return return an AmqpAdmin bean using caching connection factory
     */
    @Bean
    public AmqpAdmin amqpAdmin(ConnectionFactory connectionFactory) {
        return new RabbitAdmin(connectionFactory);
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
     * @param rabbitTemplate
     * @return
     */
    @Bean
    public RabbitMessagingTemplate rabbitMessagingTemplate(RabbitTemplate rabbitTemplate) {
        RabbitMessagingTemplate rabbitMessagingTemplate = new RabbitMessagingTemplate();
        rabbitMessagingTemplate.setMessageConverter(listenerMessageConverter());
        rabbitMessagingTemplate.setRabbitTemplate(rabbitTemplate);
        return rabbitMessagingTemplate;
    }

    /**
     * @return
     */
    @Bean
    public MappingJackson2MessageConverter listenerMessageConverter() {
        return new MappingJackson2MessageConverter();
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

    /**
     * @return
     */
    @Bean
    public Queue preprocessQueue() {
        return new Queue(Step.Phase.PRE_PROCESSING.value());
    }

    /**
     * @return
     */
    @Bean
    public Binding preprocessBind() {
        return BindingBuilder
                .bind(preprocessQueue())
                .to(orderExchange())
                .with(Step.Phase.PRE_PROCESSING.value());
    }

    /**
     * @return
     */
    @Bean
    public Queue processQueue() {
        return new Queue(Step.Phase.PROCESSING.value());
    }

    /**
     * @return
     */
    @Bean
    public Binding processBind() {
        return BindingBuilder
                .bind(processQueue())
                .to(orderExchange())
                .with(Step.Phase.PROCESSING.value());
    }

    /**
     * @return
     */
    @Bean
    public Queue postprocessQueue() {
        return new Queue(Step.Phase.POST_PROCESSING.value());
    }

    /**
     * @return
     */
    @Bean
    public Binding postprocessBind() {
        return BindingBuilder
                .bind(postprocessQueue())
                .to(orderExchange())
                .with(Step.Phase.POST_PROCESSING.value());
    }
}
