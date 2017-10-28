package net.samhouse.db.dao;

import net.samhouse.model.Order;
import net.samhouse.model.Step;

import java.util.List;

/**
 * step dao interface
 */
public interface StepDao extends EntityDao<Step> {
    /**
     * Cannot insert a step record without an order inserted before
     * TODO The smell is not so good...
     *
     * @param step
     */
    @Override
    default void insert(Step step) {
        throw new UnsupportedOperationException();
    }

    ;

    /**
     * @param order
     */
    void insertCurrentStep(Order order);

    /**
     * @param order
     */
    void insertHistoryStep(Order order);

    /**
     * @param orderID
     * @return
     */
    Step findCurrentStep(String orderID);

    /**
     * @param orderID
     * @return
     */
    List<Step> findHistorySteps(String orderID);
}
