package net.samhouse.db.service;

import net.samhouse.model.Order;
import net.samhouse.model.Step;

import java.util.List;


/**
 * step service interface
 */
public interface StepService extends EntityService<Step> {
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
