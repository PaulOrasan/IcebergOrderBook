import java.time.Instant;

public class TimeUtils {

    private TimeUtils() {

    }

    public static long getCurrentTimestamp() {
        return System.nanoTime();
    }
}
