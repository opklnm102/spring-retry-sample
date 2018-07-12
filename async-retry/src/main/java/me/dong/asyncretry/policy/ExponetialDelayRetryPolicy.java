package me.dong.asyncretry.policy;

import me.dong.asyncretry.RetryContext;

/**
 * Created by ethan.kim on 2018. 7. 10..
 */
public class ExponetialDelayRetryPolicy implements RetryPolicy {

    private final long initialDelayMills;

    private final double multiplier;

    public ExponetialDelayRetryPolicy(long initialDelayMills, double multiplier) {
        this.initialDelayMills = initialDelayMills;
        this.multiplier = multiplier;
    }

    @Override
    public long delayMillis(RetryContext context) {
        return (long) (initialDelayMills * Math.pow(multiplier, context.getRetryCount() - 1));
    }
}
