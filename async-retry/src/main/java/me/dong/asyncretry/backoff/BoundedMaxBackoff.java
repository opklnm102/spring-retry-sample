package me.dong.asyncretry.backoff;

import me.dong.asyncretry.RetryContext;

/**
 * Created by ethan.kim on 2018. 7. 17..
 */
public class BoundedMaxBackoff extends BackoffWrapper {

    public static final long DEFAULT_MAX_DELAY_MILLIS = 10_000;

    private final long maxDelayMillis;

    public BoundedMaxBackoff(Backoff target) {
        this(target, DEFAULT_MAX_DELAY_MILLIS);
    }

    public BoundedMaxBackoff(Backoff target, long maxDelayMillis) {
        super(target);
        this.maxDelayMillis = maxDelayMillis;
    }

    @Override
    public long delayMillis(RetryContext context) {
        return Math.min(target.delayMillis(context), maxDelayMillis);
    }
}
