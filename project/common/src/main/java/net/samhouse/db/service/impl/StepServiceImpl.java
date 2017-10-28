package net.samhouse.db.service.impl;

import net.samhouse.db.dao.StepDao;
import net.samhouse.db.service.StepService;
import net.samhouse.model.Order;
import net.samhouse.model.Step;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * step service, delegate to step dao
 */
@Service
public class StepServiceImpl implements StepService {

    @Autowired
    StepDao stepDao;

    /**
     * Not implemented
     *
     * @param step
     */
    @Override
    public void insert(Step step) {
        stepDao.insert(step);
    }

    /**
     * @param orderID
     * @return
     */
    @Override
    public Step getById(String orderID) {
        return stepDao.findById(orderID);
    }

    /**
     * @param order
     */
    @Override
    public void insertCurrentStep(Order order) {
        stepDao.insertCurrentStep(order);
    }

    /**
     * @param order
     */
    @Override
    public void insertHistoryStep(Order order) {
        stepDao.insertCurrentStep(order);
    }

    /**
     * @param orderID
     * @return
     */
    @Override
    public Step findCurrentStep(String orderID) {
        return stepDao.findCurrentStep(orderID);
    }

    /**
     * @param orderID
     * @return
     */
    @Override
    public List<Step> findHistorySteps(String orderID) {
        return stepDao.findHistorySteps(orderID);
    }

    /**
     * @param orderID
     */
    @Override
    public void delete(String orderID) {
        stepDao.delete(orderID);
    }
}
