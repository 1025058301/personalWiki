package per.cy.personalwiki.utils;
import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

public class DelayedMessage implements Delayed {
    private final String message;
    private final long startTime;

    private final int retryTime;

    public DelayedMessage(String message, long delay, TimeUnit unit,int retryTime) {
        this.message = message;
        this.startTime = System.currentTimeMillis() + unit.toMillis(delay);
        this.retryTime=retryTime;
    }

    public String getMessage() {
        return message;
    }
    public int getRetryTime(){
        return retryTime;
    }

    @Override
    public long getDelay(TimeUnit unit) {
        return unit.convert(startTime - System.currentTimeMillis(), TimeUnit.MILLISECONDS);
    }

    @Override
    public int compareTo(Delayed other) {
        return Long.compare(this.getDelay(TimeUnit.MILLISECONDS), other.getDelay(TimeUnit.MILLISECONDS));
    }
}
