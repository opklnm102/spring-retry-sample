package me.dong.asyncretry.policy;

import java.util.function.Predicate;

import me.dong.asyncretry.RetryContext;
import me.dong.asyncretry.policy.exception.AbortPredicateRetryPolicy;
import me.dong.asyncretry.policy.exception.ExceptionClassRetryPolicy;

/**
 * 이해가 안돼는 interface다.. default method만 있는데..
 * 이게 default method의 올바른 사용법이 맞는가..?
 * <p>
 * Created by ethan.kim on 2018. 7. 10..
 */
public interface RetryPolicy {

    public static final RetryPolicy DEFAULT = new RetryInfinitelyPolicy();

    boolean shouldContinue(RetryContext context);

    default RetryPolicy retryFor(Class<Throwable> retryForThrowable) {
        return ExceptionClassRetryPolicy.retryFor(this, retryForThrowable);
    }

    default RetryPolicy abortFor(Class<Throwable> retryForThrowable) {
        return ExceptionClassRetryPolicy.abortFor(this, retryForThrowable);
    }

    default RetryPolicy abortIf(Predicate<Throwable> retryPredicate) {
        return new AbortPredicateRetryPolicy(this, retryPredicate);
    }

    default RetryPolicy dontRetry() {
        return withMaxRetries(0);
    }

    default RetryPolicy withMaxRetries(int times) {
        return new MaxRetriesPolicy(this, times);
    }
}
