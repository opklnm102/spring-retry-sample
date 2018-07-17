package me.dong.asyncretry.backoff;

import me.dong.asyncretry.RetryContext;
import me.dong.asyncretry.policy.RetryPolicy;

/**
 * Created by ethan.kim on 2018. 7. 10..
 */
public class FixedIntervalBackoff implements Backoff {

    public static final long DEFAULT_PERIOD_MILLS = 1000;

    private final long intervalMills;

    public FixedIntervalBackoff() {
        this(DEFAULT_PERIOD_MILLS);
    }

    public FixedIntervalBackoff(long intervalMills) {
        this.intervalMills = intervalMills;
    }

    @Override
    public long delayMillis(RetryContext context) {
        return intervalMills;
    }
}
