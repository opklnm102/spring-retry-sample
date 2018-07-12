package me.dong.asyncretry.policy;

import java.util.function.Predicate;

import me.dong.asyncretry.RetryContext;
import me.dong.asyncretry.policy.exception.AbortPredicateRetryPolicy;
import me.dong.asyncretry.policy.exception.ExceptionClassRetryPolicy;
import me.dong.asyncretry.policy.random.ProportionalRandomJitterRetryPolicy;
import me.dong.asyncretry.policy.random.UniformRandomJitterRetryPolicy;

/**
 * 이해가 안돼는 interface다.. default method만 있는데..
 * 이게 default method의 올바른 사용법이 맞는가..?
 * <p>
 * Created by ethan.kim on 2018. 7. 10..
 */
public interface RetryPolicy {

    RetryPolicy DEFAULT = new FixedIntervalRetryPolicy();

    long delayMillis(RetryContext context);

    default boolean shouldContinue(RetryContext context) {
        return true;
    }

    default RetryPolicy retryFor(Class<Throwable> retryForThrowable) {
        return ExceptionClassRetryPolicy.retryFor(this, retryForThrowable);
    }

    default RetryPolicy abortFor(Class<Throwable> retryForThrowable) {
        return ExceptionClassRetryPolicy.abortFor(this, retryForThrowable);
    }

    default RetryPolicy abortIf(Predicate<Throwable> retryPredicate) {
        return new AbortPredicateRetryPolicy(this, retryPredicate);
    }

    default RetryPolicy withUniformJitter() {
        return new UniformRandomJitterRetryPolicy(this);
    }

    default RetryPolicy withUniformJitter(long range) {
        return new UniformRandomJitterRetryPolicy(this, range);
    }

    default RetryPolicy withProportionalJitter() {
        return new ProportionalRandomJitterRetryPolicy(this);
    }

    default RetryPolicy withProportionalJitter(double multiplier) {
        return new ProportionalRandomJitterRetryPolicy(this, multiplier);
    }

    default RetryPolicy withMinDelay(long minDelayMillis) {
        return new BoundedMinDelayPolicy(this, minDelayMillis);
    }

    default RetryPolicy withMaxDelay(long maxDelayMillis) {
        return new BoundedMaxDelayPolicy(this, maxDelayMillis);
    }

    default RetryPolicy withMaxRetries(int times) {
        return new MaxRetriesPolicy(this, times);
    }

    default RetryPolicy dontRetry() {
        return withMaxRetries(0);
    }
}
