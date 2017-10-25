package net.samhouse;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Utils {
    /**
     *
     * @param time
     * @return
     */
    public static String timeToString(long time) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        return format.format(new Date(time));
    }
}
