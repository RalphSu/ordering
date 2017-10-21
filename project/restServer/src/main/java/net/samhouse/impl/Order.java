package net.samhouse.impl;

import java.util.Date;

/**
 *
 */
public class Order {

    private String orderID;

    private Date startTime;

    private Date completeTime;

    public Order() {

    }

    public Order(String orderID, Date startTime, Date completeTime) {
        this.orderID = orderID;
        this.startTime = startTime;
        this.completeTime = completeTime;
    }

    public String getOrderID() {
        return orderID;
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getCompleteTime() {
        return completeTime;
    }

    public void setCompleteTime(Date completeTime) {
        this.completeTime = completeTime;
    }

    // each step needs 5s
    // each step has 5% of failure ratio
    // public Step getCurrentStep()...
}
