package net.samhouse.db.dao.impl;

import net.samhouse.db.dao.OrderDao;
import net.samhouse.db.dao.StepDao;
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
 * order dao implementation
 */
@Repository
public class OrderDaoImpl extends JdbcDaoSupport implements OrderDao {

    private static Logger log = LoggerFactory.getLogger(OrderDaoImpl.class);

    @Autowired
    private StepDao stepDao;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     *
     */
    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    /**
     * Insert an order, as steps are referenced to order,
     * so insert steps as well
     *
     * @param order
     */
    @Override
    public void insert(Order order) {
        String sql = "INSERT INTO MY_ORDER " +
                "(ORDER_ID, BEGIN_TIME, END_TIME, PAYLOAD) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(sql,
                new Object[]{order.getOrderID(), order.getStartTime(),
                        order.getCompleteTime(), order.getPayLoad()});
        stepDao.insertCurrentStep(order);
        stepDao.insertHistoryStep(order);
    }

    /**
     * Find an order according to the order id
     *
     * @param orderID
     * @return
     */
    @Override
    public Order findById(String orderID) {
        String sql = "SELECT * FROM MY_ORDER WHERE ORDER_ID = ?";
        return jdbcTemplate.queryForObject(sql, new Object[]{orderID}, new OrderRowMapper(stepDao));
    }

    /**
     * Delete an order according to the order id
     * Before delete order, we need to delete steps attached on this order
     *
     * @param orderID
     */
    @Override
    public void delete(String orderID) {
        String sql = "DELETE FROM MY_ORDER WHERE ORDER_ID = ?";
        stepDao.delete(orderID);
        jdbcTemplate.update(sql, new Object[]{orderID});
    }
}
