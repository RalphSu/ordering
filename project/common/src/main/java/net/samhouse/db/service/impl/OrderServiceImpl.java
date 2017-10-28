package net.samhouse.db.service.impl;

import net.samhouse.db.dao.OrderDao;
import net.samhouse.db.service.OrderService;
import net.samhouse.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * order service implementaion
 * delegate to order dao
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
    public void delete(String orderID) {
        orderDao.delete(orderID);
    }

    /**
     * @param orderID
     */
    @Override
    public Order getById(String orderID) {
        return orderDao.findById(orderID);
    }
}
