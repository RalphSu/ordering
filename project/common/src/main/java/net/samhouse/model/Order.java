package net.samhouse.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Order POJO
 */
public class Order implements Serializable {

    /**
     * represents current order id, should be a uuid value
     */
    private String orderID;

    /**
     * order's start processing time
     */
    private long startTime;

    /**
     * order's completed processing time
     */
    private long completeTime;

    /**
     * order's currentStep, please refer to Step definition for more detail
     */
    private Step currentStep;

    /**
     * array list to store steps completed, generally,
     * there won't be more than 5 steps changing an order from sheduling to completed
     * so, set the initial capacity to 6
     */
    private List<Step> steps = new ArrayList<>(6);

    /**
     * set an scheduling order
     */
    public Order() {
        this.orderID = UUID.randomUUID().toString();
        this.startTime = System.currentTimeMillis();
        this.completeTime = this.startTime;
        this.currentStep = new Step(this.startTime);
    }

    /**
     * @param orderID
     * @param startTime
     * @param completeTime
     * @param currentStep
     */
    public Order(String orderID, long startTime, long completeTime, Step currentStep) {
        this.orderID = orderID;
        this.startTime = startTime;
        this.completeTime = completeTime;
        this.currentStep = currentStep;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(long completeTime) {
        this.completeTime = completeTime;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Step> getSteps() {
        return steps;
    }

    public Step getCurrentStep() {
        return this.currentStep;
    }

    /**
     * as step phase changed, we need to add an item to step list
     * @param currentStep
     */
    public void setCurrentStep(Step currentStep) {
        if (currentStep.getCurrentPhase() != this.currentStep.getCurrentPhase()) {
            steps.add(this.currentStep.setCurrentPhase(currentStep.getCurrentPhase()));
        }
    }

    /**
     * set the currentStep to failed directly and add an item to step list
     */
    public void setToFailed() {
        if (currentStep.getCurrentPhase() != Step.Phase.FAILED) {
            steps.add(currentStep.setCurrentPhase(Step.Phase.FAILED));
        }
        setCompleteTime(System.currentTimeMillis());
    }

    /**
     * Move to the next currentStep, if current currentStep is completed, then
     * next currentStep will be set to default failed
     */
    public void moveToNextStep() {
        steps.add(currentStep.moveToNextPhase());
    }
}
