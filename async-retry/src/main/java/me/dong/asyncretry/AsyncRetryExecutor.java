package me.dong.asyncretry;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

import me.dong.asyncretry.backoff.Backoff;
import me.dong.asyncretry.policy.RetryPolicy;

/**
 * Created by ethan.kim on 2018. 7. 9..
 */
public class AsyncRetryExecutor implements RetryExecutor {

    private final ScheduledExecutorService scheduler;

    private final boolean fixedDelay;

    private final RetryPolicy retryPolicy;

    private final Backoff backoff;

    public AsyncRetryExecutor(ScheduledExecutorService scheduler) {
        this(scheduler, RetryPolicy.DEFAULT, Backoff.DEFAULT);
    }

    public AsyncRetryExecutor(ScheduledExecutorService scheduler, Backoff backoff) {
        this(scheduler, RetryPolicy.DEFAULT, backoff);
    }

    public AsyncRetryExecutor(ScheduledExecutorService scheduler, RetryPolicy retryPolicy) {
        this(scheduler, retryPolicy, Backoff.DEFAULT);
    }

    public AsyncRetryExecutor(ScheduledExecutorService scheduler, RetryPolicy retryPolicy, Backoff backoff) {
        this(scheduler, retryPolicy, backoff, false);
    }

    public AsyncRetryExecutor(ScheduledExecutorService scheduler, RetryPolicy retryPolicy, Backoff backoff, boolean fixedDelay) {
        this.scheduler = Objects.requireNonNull(scheduler);
        this.retryPolicy = Objects.requireNonNull(retryPolicy);
        this.backoff = Objects.requireNonNull(backoff);
        this.fixedDelay = fixedDelay;
    }

    @Override
    public CompletableFuture<Void> doWithRetry(Consumer<RetryContext> consumer) {
        return getWithRetry(context -> {
            consumer.accept(context);
            return null;
        });
    }

    @Override
    public <V> CompletableFuture<V> getWithRetry(Supplier<V> supplier) {
        return getWithRetry(context -> supplier.get());
    }

    @Override
    public <V> CompletableFuture<V> getWithRetry(Function<RetryContext, V> function) {
        return scheduleImmediately(function);
    }

    private <V> CompletableFuture<V> scheduleImmediately(Function<RetryContext, V> function) {
        final RetryTask<V> task = new RetryTask<>(function, this);
        scheduler.schedule(task, 0, TimeUnit.MILLISECONDS);
        return task.getFuture();
    }

    public ScheduledExecutorService getScheduler() {
        return scheduler;
    }

    public boolean isFixedDelay() {
        return fixedDelay;
    }

    public RetryPolicy getRetryPolicy() {
        return retryPolicy;
    }

    public Backoff getBackoff() {
        return backoff;
    }

    public AsyncRetryExecutor withScheduler(ScheduledExecutorService scheduler) {
        return new AsyncRetryExecutor(scheduler, retryPolicy, backoff, fixedDelay);
    }

    public AsyncRetryExecutor withRetryPolicy(RetryPolicy retryPolicy) {
        return new AsyncRetryExecutor(scheduler, retryPolicy, backoff, fixedDelay);
    }

    public AsyncRetryExecutor withBackoff(Backoff backoff) {
        return new AsyncRetryExecutor(scheduler, retryPolicy, backoff, fixedDelay);
    }

    public AsyncRetryExecutor withFixedDelay() {
        return new AsyncRetryExecutor(scheduler, retryPolicy, backoff, true);
    }

    public AsyncRetryExecutor withFixedDelay(boolean fixedDelay) {
        return new AsyncRetryExecutor(scheduler, retryPolicy, backoff, fixedDelay);
    }

    public AsyncRetryExecutor retryFor(Class<Throwable> retryForThrowable) {
        return this.withRetryPolicy(retryPolicy.retryFor(retryForThrowable));
    }

    public AsyncRetryExecutor abortFor(Class<Throwable> abortForThrowable) {
        return this.withRetryPolicy(this.retryPolicy.abortFor(abortForThrowable));
    }

    public AsyncRetryExecutor abortIf(Predicate<Throwable> abortPredicate) {
        return this.withRetryPolicy(retryPolicy.abortIf(abortPredicate));
    }

    public AsyncRetryExecutor withUniformJitter() {
        return this.withBackoff(this.backoff.withUniformJitter());
    }

    public AsyncRetryExecutor withUniformJitter(long range) {
        return this.withBackoff(this.backoff.withUniformJitter(range));
    }

    public AsyncRetryExecutor withProportionalJitter() {
        return this.withBackoff(this.backoff.withProportionalJitter());
    }

    public AsyncRetryExecutor withProportionalJitter(double multiplier) {
        return this.withBackoff(this.backoff.withProportionalJitter(multiplier));
    }

    public AsyncRetryExecutor withMinDelay(long minDelayMills) {
        return this.withBackoff(this.backoff.withMinDelay(minDelayMills));
    }

    public AsyncRetryExecutor withMaxDelay(long maxDelayMillis) {
        return this.withBackoff(this.backoff.withMaxDelay(maxDelayMillis));
    }

    public AsyncRetryExecutor withMaxRetries(int times) {
        return this.withRetryPolicy(this.retryPolicy.withMaxRetries(times));
    }

    public AsyncRetryExecutor dontRetry() {
        return this.withRetryPolicy(this.retryPolicy.dontRetry());
    }
}
