package me.dong.asyncretry.policy;

import me.dong.asyncretry.RetryContext;

/**
 * Created by ethan.kim on 2018. 7. 10..
 */
public class BoundedMinDelayPolicy extends RetryPolicyWrapper {

    public static final long DEFAULT_MIN_DELAY_MILLS = 100;

    private final long minDelayMills;

    public BoundedMinDelayPolicy(RetryPolicy target) {
        this(target, DEFAULT_MIN_DELAY_MILLS);
    }

    public BoundedMinDelayPolicy(RetryPolicy target, long minDelayMills) {
        super(target);
        this.minDelayMills = minDelayMills;
    }

    @Override
    public long delayMillis(RetryContext context) {
        return Math.max(target.delayMillis(context), minDelayMills);
    }
}
