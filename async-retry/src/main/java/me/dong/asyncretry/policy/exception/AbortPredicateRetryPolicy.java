package me.dong.asyncretry.policy.exception;

import java.util.function.Predicate;

import me.dong.asyncretry.RetryContext;
import me.dong.asyncretry.policy.RetryPolicy;
import me.dong.asyncretry.policy.RetryPolicyWrapper;

/**
 * Created by ethan.kim on 2018. 7. 10..
 */
public class AbortPredicateRetryPolicy extends RetryPolicyWrapper {

    private final Predicate<Throwable> abortPredicate;

    public AbortPredicateRetryPolicy(RetryPolicy target, Predicate<Throwable> abortPredicate) {
        super(target);
        this.abortPredicate = abortPredicate;
    }

    @Override
    public boolean shouldContinue(RetryContext context) {
        return !abortPredicate.test(context.getLastThrowable())
                && tryNestedPolicyIfAlsoPredicate(context);
    }

    private boolean tryNestedPolicyIfAlsoPredicate(RetryContext context) {
        return target instanceof AbortPredicateRetryPolicy
                && target.shouldContinue(context);
    }
}
