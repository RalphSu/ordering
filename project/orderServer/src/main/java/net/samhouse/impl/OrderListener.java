package net.samhouse.impl;

import net.samhouse.model.Order;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageListener;

public class OrderListener implements MessageListener{
    @Override
    public void onMessage(Message message) {
        System.out.println(message);
    }
}
