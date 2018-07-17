package me.dong.asyncretry;

import me.dong.asyncretry.policy.RetryPolicy;

/**
 * Created by ethan.kim on 2018. 7. 9..
 */
public class AsyncRetryContext implements RetryContext {

    private final RetryPolicy retryPolicy;

    private final int retry;

    private final Throwable lastThrowable;

    public AsyncRetryContext(RetryPolicy retryPolicy) {
        this(retryPolicy, 0, null);
    }

    public AsyncRetryContext(RetryPolicy retryPolicy, int retry, Throwable lastThrowable) {
        this.retryPolicy = retryPolicy;
        this.retry = retry;
        this.lastThrowable = lastThrowable;
    }

    @Override
    public boolean willRetry() {
        return retryPolicy.shouldContinue(this.nextRetry(new Exception()));
    }

    @Override
    public int getRetryCount() {
        return retry;
    }

    @Override
    public Throwable getLastThrowable() {
        return lastThrowable;
    }

    public AsyncRetryContext nextRetry(Throwable cause) {
        return new AsyncRetryContext(retryPolicy, retry + 1, cause);
    }
}
