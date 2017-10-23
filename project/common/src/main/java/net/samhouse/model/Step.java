package net.samhouse.model;

import java.io.Serializable;
import java.util.Date;
import java.util.stream.Stream;

/**
 * POJO for Steps
 */
public class Step implements Serializable {

    /**
     * enumeration for step phases, we have scheduling,
     * pre-processing, processing, post-processing phases, completed and failed
     * the last two phase are used for failure and completion
     */
    public enum Phase {
        SCHEDULING("SCHEDULING"),
        PRE_PROCESSING("PRE_PROCESSING"),
        PROCESSING("PROCESSING"),
        POST_PROCESSING("POST_PROCESSING"),
        COMPLETED("COMPLETED"),
        FAILED("FAILED");

        private final String phase;

        /**
         * @param step
         */
        private Phase(String step) {
            this.phase = step;
        }

        public String value() {return phase;};

        /**
         *
         * @param phase
         * @return
         */
        public static Phase fromValue(String phase) {
            return Stream.of(Phase.values())
                    .filter(p -> p.value().equals(phase)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(phase));
        }
    }

    /**
     * step start time
     */
    private long startTime;

    /**
     * step complete time
     */
    private long completeTime;

    /**
     * Current phase the step in
     */
    private Phase currentPhase;

    public Step() {
        this.startTime = System.currentTimeMillis();
        this.completeTime = startTime;
        currentPhase = Phase.SCHEDULING;
    }

    public Step(long startTime) {
        this.startTime = startTime;
        this.completeTime = startTime;
        currentPhase = Phase.SCHEDULING;
    }

    /**
     *
     * @param currentPhase
     * @param startTime
     * @param completeTime
     */
    public Step(Phase currentPhase, long startTime, long completeTime) {
        this.currentPhase = currentPhase;
        this.startTime = startTime;
        this.completeTime = completeTime;
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

    public Phase getCurrentPhase() {
        return currentPhase;
    }

    /**
     *
     * @param currentPhase
     * @return return an either new or this step object
     */
    public Step setCurrentPhase(Phase currentPhase) {

        // save current time
        long currentTime = System.currentTimeMillis();

        // if phase is changed, then return a new Step object
        if (this.currentPhase != currentPhase) {
            // save current status to a new Step
            // and set the complete time of new step to current time
            Step step = new Step(this.currentPhase, this.startTime, currentTime);

            this.startTime = currentTime;
            this.completeTime = currentTime;
            this.currentPhase = currentPhase;

            return step;
        }

        return this;
    }

    /**
     *
     * @return return a new Step object
     */
    public Step moveToNextPhase() {
        switch (currentPhase) {
            case SCHEDULING:
                return setCurrentPhase(Phase.PRE_PROCESSING);
            case PRE_PROCESSING:
                return setCurrentPhase(Phase.PROCESSING);
            case PROCESSING:
                return setCurrentPhase(Phase.POST_PROCESSING);
            case POST_PROCESSING:
                return setCurrentPhase(Phase.COMPLETED);
            default:
                return setCurrentPhase(Phase.FAILED);
        }
    }
}
