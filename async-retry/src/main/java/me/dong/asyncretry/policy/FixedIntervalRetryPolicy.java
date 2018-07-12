package me.dong.asyncretry.policy;

import me.dong.asyncretry.RetryContext;

/**
 * Created by ethan.kim on 2018. 7. 10..
 */
public class FixedIntervalRetryPolicy implements RetryPolicy {

    public static final long DEFAULT_PERIOD_MILLS = 1000;

    private final long intervalMills;

    public FixedIntervalRetryPolicy() {
        this(DEFAULT_PERIOD_MILLS);
    }

    public FixedIntervalRetryPolicy(long intervalMills) {
        this.intervalMills = intervalMills;
    }

    @Override
    public long delayMillis(RetryContext context) {
        return intervalMills;
    }
}
