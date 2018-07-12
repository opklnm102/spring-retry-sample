package me.dong.asyncretry.policy;

import java.util.Objects;

import me.dong.asyncretry.RetryContext;

/**
 * Created by ethan.kim on 2018. 7. 10..
 */
public abstract class RetryPolicyWrapper implements RetryPolicy {

    protected final RetryPolicy target;

    protected RetryPolicyWrapper(RetryPolicy target) {
        this.target = Objects.requireNonNull(target);
    }

    @Override
    public long delayMillis(RetryContext context) {
        return target.delayMillis(context);
    }

    @Override
    public boolean shouldContinue(RetryContext context) {
        return target.shouldContinue(context);
    }
}
