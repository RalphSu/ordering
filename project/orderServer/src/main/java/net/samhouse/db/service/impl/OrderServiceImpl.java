package net.samhouse.db.service.impl;

import net.samhouse.db.dao.OrderDao;
import net.samhouse.db.service.OrderService;
import net.samhouse.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 *
 */
@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    OrderDao orderDao;

    /**
     * @param order
     */
    @Override
    public void insert(Order order) {
        orderDao.insert(order);
    }

    /**
     * @param orderID
     */
    @Override
    public void getOrderById(String orderID) {
        Order order = orderDao.findOrderById(orderID);
    }
}
