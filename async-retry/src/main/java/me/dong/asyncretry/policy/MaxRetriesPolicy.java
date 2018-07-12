package me.dong.asyncretry.policy;

import me.dong.asyncretry.RetryContext;

/**
 * Created by ethan.kim on 2018. 7. 10..
 */
public class MaxRetriesPolicy extends RetryPolicyWrapper {

    public static final int DEFAULT_MAX_RETRIES = 10;

    private final int maxRetries;

    public MaxRetriesPolicy(RetryPolicy target) {
        this(target, DEFAULT_MAX_RETRIES);
    }

    public MaxRetriesPolicy(RetryPolicy target, int maxRetries) {
        super(target);
        this.maxRetries = maxRetries;
    }

    @Override
    public boolean shouldContinue(RetryContext context) {
        return target.shouldContinue(context) && context.getRetryCount() <= maxRetries;
    }
}
