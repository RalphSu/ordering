package net.samhouse.db.dao.impl;

import net.samhouse.db.dao.StepDao;
import net.samhouse.model.Order;
import net.samhouse.model.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.support.JdbcDaoSupport;
import org.springframework.stereotype.Repository;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;

/**
 * step dao
 * I don't want to move these sqls out from functions
 * coz it will be more clear for the functionality of each function
 * TODO we could provide more queries...
 */
@Repository
public class StepDaoImpl extends JdbcDaoSupport implements StepDao {

    private static Logger log = LoggerFactory.getLogger(StepDaoImpl.class);

    @Autowired
    private DataSource dataSource;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @PostConstruct
    private void initialize() {
        setDataSource(dataSource);
    }

    /**
     * insert the current step of an order to db
     *
     * @param order
     */
    public void insertCurrentStep(Order order) {
        String sql = "INSERT INTO MY_STEP " +
                "(ORDER_ID, PHASE, BEGIN_TIME, END_TIME, IS_CURRENT) VALUES (?, ?, ?, ?, ?)";
        jdbcTemplate.update(sql, new Object[]{
                order.getOrderID(),
                order.getCurrentStep().getCurrentPhase().value(),
                order.getCurrentStep().getStartTime(),
                order.getCurrentStep().getCompleteTime(),
                true});
    }

    /**
     * insert all the history steps of an order to db
     *
     * @param order
     */
    public void insertHistoryStep(Order order) {
        String sql = "INSERT INTO MY_STEP " +
                "(ORDER_ID, PHASE, BEGIN_TIME, END_TIME, IS_CURRENT) VALUES (?, ?, ?, ?, ?)";

        order.getSteps().stream().forEach(step -> {
            jdbcTemplate.update(sql, new Object[]{
                    order.getOrderID(),
                    step.getCurrentPhase().value(),
                    step.getStartTime(),
                    step.getCompleteTime(),
                    false});
        });
    }

    /**
     * find a step according to order id
     *
     * @param orderID
     * @return
     */
    @Override
    public Step findById(String orderID) {
        String sql = "SELECT * FROM MY_STEP WHERE ORDER_ID = ?";
        return (Step) jdbcTemplate.queryForObject(sql, new Object[]{orderID}, new StepRowMapper());
    }

    /**
     * find an order's current step
     *
     * @param orderID
     * @return
     */
    public Step findCurrentStep(String orderID) {
        String sql = "SELECT * FROM MY_STEP WHERE ORDER_ID = ? and IS_CURRENT = ?";
        return (Step) jdbcTemplate.queryForObject(sql, new Object[]{orderID, true}, new StepRowMapper());
    }

    /**
     * find an order's history steps
     *
     * @param orderID
     * @return
     */
    public List<Step> findHistorySteps(String orderID) {
        String sql = "SELECT * FROM MY_STEP WHERE ORDER_ID = ? and IS_CURRENT = ?";
        return jdbcTemplate.query(sql, new Object[]{orderID, false}, new StepRowMapper());
    }

    /**
     * delete all the steps of an order
     *
     * @param orderID
     */
    @Override
    public void delete(String orderID) {
        String sql = "DELETE FROM MY_STEP WHERE ORDER_ID = ?";
        jdbcTemplate.update(sql, new Object[]{orderID});
    }
}
