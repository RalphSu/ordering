package net.samhouse.controller;

import net.samhouse.db.service.OrderService;
import net.samhouse.model.Order;
import net.samhouse.model.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 *
 */
@Controller
@RequestMapping("/")
public class OrderRestController {
    private static Logger log = LoggerFactory.getLogger(OrderRestController.class);

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private OrderService orderService;

    /**
     * @param orderId
     * @param user
     * @return
     */
    @RequestMapping(path = "/query/{orderId}", method = RequestMethod.GET)
    public @ResponseBody
    Order queryOrder(@PathVariable final String orderId,
                     @AuthenticationPrincipal final UserDetails user) {

        log.info("get order query[{}]", orderId);
        return orderService.getById(orderId);
    }

    /**
     * @param order
     * @param user
     * @return
     */
    @RequestMapping(path = "/submit", method = RequestMethod.POST)
    @ResponseBody
    String submitOrder(@RequestBody Order order,
                       @AuthenticationPrincipal final UserDetails user) {
        // get payload from request
        String paylaod = order.getPayLoad();
        order.init();
        if (paylaod != null && !paylaod.isEmpty()) {
            order.setPayLoad(paylaod);
        }

        log.info("submit order[{}, {}]",
                order.getOrderID(), order.getPayLoad());

        rabbitTemplate.convertAndSend(Step.SCHEDULING, order);
        return order.getOrderID();
    }
}
