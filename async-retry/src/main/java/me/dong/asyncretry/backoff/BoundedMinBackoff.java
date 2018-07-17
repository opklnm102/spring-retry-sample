package me.dong.asyncretry.backoff;

import me.dong.asyncretry.RetryContext;
import me.dong.asyncretry.policy.RetryPolicy;
import me.dong.asyncretry.policy.RetryPolicyWrapper;

/**
 * Created by ethan.kim on 2018. 7. 10..
 */
public class BoundedMinBackoff extends BackoffWrapper {

    public static final long DEFAULT_MIN_DELAY_MILLS = 100;

    private final long minDelayMills;

    public BoundedMinBackoff(Backoff target) {
        this(target, DEFAULT_MIN_DELAY_MILLS);
    }

    public BoundedMinBackoff(Backoff target, long minDelayMills) {
        super(target);
        this.minDelayMills = minDelayMills;
    }

    @Override
    public long delayMillis(RetryContext context) {
        return Math.max(target.delayMillis(context), minDelayMills);
    }
}
