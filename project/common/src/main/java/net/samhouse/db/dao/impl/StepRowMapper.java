package net.samhouse.db.dao.impl;

import net.samhouse.model.Step;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * step row mapper
 */
public final class StepRowMapper implements RowMapper<Step> {

    /**
     * @param resultSet
     * @param rowNumber
     * @return
     * @throws SQLException
     */
    @Override
    public Step mapRow(ResultSet resultSet, int rowNumber) throws SQLException {
        Step step = new Step();
        step.setCurrentPhase(Step.Phase.fromValue(resultSet.getString("PHASE")));
        step.setStartTime(resultSet.getLong("BEGIN_TIME"));
        step.setCompleteTime(resultSet.getLong("END_TIME"));
        return step;
    }
}
