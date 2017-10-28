package net.samhouse.db.dao.impl;

import net.samhouse.db.dao.StepDao;
import net.samhouse.model.Order;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * row mapper
 */
public final class OrderRowMapper implements RowMapper<Order> {

    private StepDao stepDao;

    public OrderRowMapper(StepDao stepDao) {
        this.stepDao = stepDao;
    }

    /**
     * Maps the order object from the result set
     *
     * @param resultSet The sql result set
     * @param rowNumber The row number
     * @return An Order object
     * @throws SQLException Thrown if there was a SQL issue during mapping
     */
    @Override
    public Order mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        Order order = new Order();
        order.setOrderID(resultSet.getString("ORDER_ID"));
        order.setStartTime(resultSet.getLong("BEGIN_TIME"));
        order.setCompleteTime(resultSet.getLong("END_TIME"));

        // as payload in db is nullable, so we need to replace null with ""
        // TODO sometimes this is discussable for replace null with ""
        String payload = resultSet.getString("PAYLOAD");
        order.setPayLoad(payload == null ? "" : payload);

        order.setCurrentStep(stepDao.findCurrentStep(order.getOrderID()));
        order.setSteps(stepDao.findHistorySteps(order.getOrderID()));
        return order;
    }
}
