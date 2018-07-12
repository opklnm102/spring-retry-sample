package me.dong.asyncretry.policy.exception;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import me.dong.asyncretry.RetryContext;
import me.dong.asyncretry.policy.RetryPolicy;
import me.dong.asyncretry.policy.RetryPolicyWrapper;

/**
 * Created by ethan.kim on 2018. 7. 11..
 */
public class ExceptionClassRetryPolicy extends RetryPolicyWrapper {

    private final Set<Class<Throwable>> retryFor;

    private final Set<Class<Throwable>> abortFor;

    public ExceptionClassRetryPolicy(RetryPolicy target, Set<Class<Throwable>> retryFor, Set<Class<Throwable>> abortFor) {
        super(target);
        this.retryFor = retryFor;
        this.abortFor = abortFor;
    }

    public static ExceptionClassRetryPolicy retryFor(RetryPolicy target, Class<Throwable> retryForThrowable) {
        if (target instanceof ExceptionClassRetryPolicy) {
            return mergeRetryForWithExisting((ExceptionClassRetryPolicy) target, retryForThrowable);
        }
        return new ExceptionClassRetryPolicy(target, Collections.singleton(retryForThrowable), Collections.emptySet());
    }

    private static ExceptionClassRetryPolicy mergeRetryForWithExisting(ExceptionClassRetryPolicy topTarget, Class<Throwable> retryForThrowable) {
        return new ExceptionClassRetryPolicy(
                topTarget.target,
                setPlusElem(topTarget.retryFor, retryForThrowable),
                topTarget.abortFor
        );
    }

    public static ExceptionClassRetryPolicy abortFor(RetryPolicy target, Class<Throwable> abortForThrowable) {
        if (target instanceof ExceptionClassRetryPolicy) {
            return mergeAbortForWithExisting((ExceptionClassRetryPolicy) target, abortForThrowable);
        }
        return new ExceptionClassRetryPolicy(target, Collections.emptySet(), Collections.singleton(abortForThrowable));
    }

    private static ExceptionClassRetryPolicy mergeAbortForWithExisting(ExceptionClassRetryPolicy topTarget, Class<Throwable> abortForThrowable) {
        return new ExceptionClassRetryPolicy(
                topTarget.target,
                topTarget.retryFor,
                setPlusElem(topTarget.abortFor, abortForThrowable)
        );
    }

    private static <T> Set<T> setPlusElem(Set<T> initial, T newElement) {
        final HashSet<T> copy = new HashSet<>(initial);
        copy.add(Objects.requireNonNull(newElement));
        return Collections.unmodifiableSet(copy);
    }

    @Override
    public boolean shouldContinue(RetryContext context) {
        if (!target.shouldContinue(context)) {
            return false;
        }

        final Class<? extends Throwable> lastThrowable = context.getLastThrowable().getClass();
        if (abortFor.isEmpty()) {
            return matches(lastThrowable, retryFor);
        }

        return !matches(lastThrowable, abortFor)
                && (retryFor.isEmpty() || matches(lastThrowable, retryFor));
    }

    private static boolean matches(Class<? extends Throwable> throwable, Set<Class<Throwable>> set) {
        return set.stream().anyMatch(c -> c.isAssignableFrom(throwable));
    }
}
