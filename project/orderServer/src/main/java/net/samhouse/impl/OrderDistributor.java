package net.samhouse.impl;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import net.samhouse.model.Order;
import net.samhouse.model.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 */
@Service
public class OrderDistributor {

    private static Logger log = LoggerFactory.getLogger(OrderDistributor.class);

    /**
     * handler thread pool size
     */
    @Value("${handler.poolsize:50}")
    private Integer poolsize = 50;

    /**
     * handler queue size
     */
    @Value("${handler.queuesize:10000}")
    private Integer queuesize = 10000;

    private final LinkedBlockingQueue<Order> orderQueue;

    @Autowired
    private OrderSender orderSender;

    private List<Handler> handlers;

    private Future poolFuture;

    private ExecutorService orderPool;

    private Map<String, Handler> handlerMap;

    @Autowired
    public void setHandlers(List<Handler> handlers) {
        this.handlers = handlers;
    }

    public OrderDistributor() {
        List<Handler> handlers = new ArrayList<>();
        handlers.add(new ScheduleHandler());
        handlers.add(new PreProcessHandler());
        handlers.add(new ProcessHandler());
        handlers.add(new PostProcessHandler());
        handlers.add(new EndStateHandler());

        handlerMap = handlers.stream()
                .collect(Collectors.toMap(Handler::getName, h -> h));

        // Using completed handler for failed as well
        // as both of them will go to db finally
        handlerMap.put(Step.FAILED, handlerMap.get(Step.COMPLETED));

        orderQueue = new LinkedBlockingQueue<>(queuesize);
        orderPool = Executors.newFixedThreadPool(poolsize,
                new ThreadFactoryBuilder().setNameFormat("orderPool-%03d").build());
        poolFuture = orderPool.submit(new OrderHandler());
    }

    public void handleOrder(Order order, String queue) {
        log.debug("Starting to preparing order[{}] from queue[{}]", order.getOrderID(), queue);

        if (!orderQueue.offer(order)) {
            log.error("Order queue is full!");
            log.error("Can not handle order[{}]", order.toString());
        }

    }

    private class OrderHandler implements Runnable {
        @Override
        public void run() {
            while (true) {
                try {
                    Order order = orderQueue.take();
                    String currentPhase = order.getCurrentStep().getCurrentPhase().value();
                    Handler handler = handlerMap.get(currentPhase);
                    if (handler == null) {
                        log.error("Can not find handler for order[{}]", order.toString());
                        continue;
                    }
                    handler.handleOrder(order, currentPhase);
                    log.debug("Complete order[{}] handling", order.getOrderID());
                    orderSender.deliverOrder(order);
                    log.debug("deliver order[{}] finished", order.getOrderID());
                } catch (InterruptedException e) {

                }
            }
        }
    }
}
