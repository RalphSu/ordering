package net.samhouse.db.dao.impl;

import net.samhouse.model.Order;
import net.samhouse.model.Step;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public final class OrderRowMapper implements RowMapper<Order> {

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
        order.setCurrentStep((Step) resultSet.getObject("STEP"));
        return order;
    }
}
