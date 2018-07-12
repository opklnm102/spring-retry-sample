package me.dong.asyncretry.policy;

import me.dong.asyncretry.RetryContext;

/**
 * Created by ethan.kim on 2018. 7. 10..
 */
public class BoundedMaxDelayPolicy extends RetryPolicyWrapper {

    public static final long DEFAULT_MAX_DELAY_MILLIS = 10_000;

    private final long maxDelayMillis;

    public BoundedMaxDelayPolicy(RetryPolicy target) {
        this(target, DEFAULT_MAX_DELAY_MILLIS);
    }

    public BoundedMaxDelayPolicy(RetryPolicy target, long maxDelayMillis) {
        super(target);
        this.maxDelayMillis = maxDelayMillis;
    }

    @Override
    public long delayMillis(RetryContext context) {
        return Math.min(target.delayMillis(context), maxDelayMillis);
    }
}
