package net.samhouse.controller;

import net.samhouse.model.Order;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/")
public class OrderRestController {

    @RequestMapping(path = "/query/{orderId}", method = RequestMethod.GET)
    public @ResponseBody
    String queryOrder(@PathVariable final String orderId,
                       @AuthenticationPrincipal final UserDetails user) {
        return orderId;
    }

    @RequestMapping(path = "/submit", method = RequestMethod.POST)
    @ResponseBody
    String submitOrder(@RequestBody Order order,
                                  @AuthenticationPrincipal final UserDetails user) {
        String orderId = UUID.randomUUID().toString();
        return "orderID";
    }
}
