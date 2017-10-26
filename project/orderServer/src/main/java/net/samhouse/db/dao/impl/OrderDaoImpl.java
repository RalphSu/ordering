package net.samhouse.db.dao.impl;

import net.samhouse.db.dao.OrderDao;
import net.samhouse.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

/**
 *
 */
@Repository
public class OrderDaoImpl extends JdbcDaoSupport implements OrderDao {

    private static Logger log = LoggerFactory.getLogger(OrderDaoImpl.class);

    @Autowired
    DataSource dataSource;

    @Autowired
    JdbcTemplate jdbcTemplate;

    /**
     *
     */
    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    /**
     * @param order
     */
    @Override
    public void insert(Order order) {
        String sql = "INSERT INTO order " +
                "(ORDER_ID, BEGIN_TIME, END_TIME, STEP, STEPS) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, new Object[]{
                order.getOrderID(), order.getStartTime(), order.getCompleteTime(),
                order.getCurrentStep(), order.getSteps()
        });
    }

    /**
     * @param orderID
     * @return
     */
    @Override
    public Order findOrderById(String orderID) {
        String sql = "SELECT * FROM order WHERE ORDER_ID = ?";
        return (Order) jdbcTemplate.queryForObject(sql, new Object[]{orderID}, new OrderRowMapper());
    }
}
