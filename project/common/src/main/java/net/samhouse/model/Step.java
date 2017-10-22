package net.samhouse.model;

import java.io.Serializable;
import java.util.Date;
import java.util.stream.Stream;

public class Step implements Serializable {
    public enum Phase {
        SCHEDULING("SCHEDULING"),
        PRE_PROCESSING("PRE_PROCESSING"),
        PROCESSING("PROCESSING"),
        POST_PROCESSING("POST_PROCESSING");

        private final String phase;

        private Phase(String step) {
            this.phase = step;
        }

        public String value() {return phase;};

        public static Phase fromValue(String phase) {
            return Stream.of(Phase.values())
                    .filter(p -> p.value().equals(phase)).findFirst()
                    .orElseThrow(() -> new IllegalArgumentException(phase));
        }
    }

    private Date startTime;

    private Date completeTime;

    private Phase phase;

    public Step() {
    }

    public Step(Phase phase, Date startTime, Date completeTime) {
        this.phase = phase;
        this.startTime = startTime;
        this.completeTime = completeTime;
    }
}
