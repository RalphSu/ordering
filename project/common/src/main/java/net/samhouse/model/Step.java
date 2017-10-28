package net.samhouse.model;

import java.io.Serializable;
import java.util.stream.Stream;

import static net.samhouse.Utils.timeToString;

/**
 * POJO for Steps
 */
public class Step implements Serializable {

    private static final long serialVersionUID = 1784911171331472918L;

    public static final String SCHEDULING = "SCHEDULING";
    public static final String PRE_PROCESSING = "PRE_PROCESSING";
    public static final String PROCESSING = "PROCESSING";
    public static final String POST_PROCESSING = "POST_PROCESSING";
    public static final String COMPLETED = "COMPLETED";
    public static final String FAILED = "FAILED";

    /**
     * enumeration for step phases, we have scheduling,
     * pre-processing, processing, post-processing phases, completed and failed
     * the last two phase are used for failure and completion
     */
    public enum Phase {
        SCHEDULING(Step.SCHEDULING),
        PRE_PROCESSING(Step.PRE_PROCESSING),
        PROCESSING(Step.PROCESSING),
        POST_PROCESSING(Step.POST_PROCESSING),
        COMPLETED(Step.COMPLETED),
        FAILED(Step.FAILED);

        private final String phase;

        /**
         * @param step
         */
        private Phase(String step) {
            this.phase = step;
        }

        public final String value() {
            return phase;
        }

        /**
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
    }

    /**
     * @return
     */
    public Step init() {
        this.startTime = System.currentTimeMillis();
        this.completeTime = startTime;
        currentPhase = Phase.SCHEDULING;
        return this;
    }

    public Step(long startTime) {
        this.startTime = startTime;
        this.completeTime = startTime;
        currentPhase = Phase.SCHEDULING;
    }

    /**
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

    public void setCurrentPhase(Phase currentPhase) {
        this.currentPhase = currentPhase;
    }

    /**
     * @param currentPhase
     * @return return an either new or this step object
     */
    public Step changeCurrentPhase(Phase currentPhase) {

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
     * @return return a new Step object
     */
    public Step moveToNextPhase() {
        switch (currentPhase) {
            case SCHEDULING:
                return changeCurrentPhase(Phase.PRE_PROCESSING);
            case PRE_PROCESSING:
                return changeCurrentPhase(Phase.PROCESSING);
            case PROCESSING:
                return changeCurrentPhase(Phase.POST_PROCESSING);
            case POST_PROCESSING:
                return changeCurrentPhase(Phase.COMPLETED);
            // Keep unchanged
            case COMPLETED:
            case FAILED:
                return this;
            default:
                return changeCurrentPhase(Phase.FAILED);
        }
    }

    /**
     * @return
     */
    public Phase GetNextPhase() {
        switch (currentPhase) {
            case SCHEDULING:
                return Phase.PRE_PROCESSING;
            case PRE_PROCESSING:
                return Phase.PROCESSING;
            case PROCESSING:
                return Phase.POST_PROCESSING;
            case POST_PROCESSING:
                return Phase.COMPLETED;
            // Keep unchanged
            case COMPLETED:
            case FAILED:
                return currentPhase;
            default:
                return Phase.FAILED;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Step step = (Step) o;

        if (startTime != step.startTime) return false;
        if (completeTime != step.completeTime) return false;
        return currentPhase != null ? currentPhase.equals(step.currentPhase) : step.currentPhase == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (startTime ^ (startTime >>> 32));
        result = 31 * result + (int) (completeTime ^ (completeTime >>> 32));
        result = 31 * result + (currentPhase != null ? currentPhase.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Step{");
        sb.append("startTime=").append(timeToString(startTime));
        sb.append(", completeTime=").append(timeToString(completeTime));
        sb.append(", currentPhase=").append(currentPhase);
        sb.append('}');
        return sb.toString();
    }
}
