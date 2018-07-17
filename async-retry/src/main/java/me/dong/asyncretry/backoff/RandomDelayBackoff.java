package me.dong.asyncretry.backoff;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;

import me.dong.asyncretry.RetryContext;
import me.dong.asyncretry.policy.RetryPolicy;
import me.dong.asyncretry.policy.RetryPolicyWrapper;

/**
 * Created by ethan.kim on 2018. 7. 11..
 */
public abstract class RandomDelayBackoff extends BackoffWrapper {

    private final Supplier<Random> randomSource;

    protected RandomDelayBackoff(Backoff target) {
        this(target, ThreadLocalRandom::current);
    }

    protected RandomDelayBackoff(Backoff target, Random randomSource) {
        this(target, () -> randomSource);
    }

    private RandomDelayBackoff(Backoff target, Supplier<Random> randomSource) {
        super(target);
        this.randomSource = randomSource;
    }

    @Override
    public long delayMillis(RetryContext context) {
        final long initialDelay = target.delayMillis(context);
        final long randomDelay = addRandomJitter(initialDelay);
        return Math.max(randomDelay, 0);
    }

    abstract long addRandomJitter(long initialDelay);

    protected Random random() {
        return randomSource.get();
    }
}
