package me.dong.asyncretry.policy.random;

import java.util.Random;

import me.dong.asyncretry.policy.RetryPolicy;

/**
 * Created by ethan.kim on 2018. 7. 10..
 */
public class ProportionalRandomJitterRetryPolicy extends RandomDelayJitterRetryPolicy {

    /**
     * Randomly up to +/- 10%
     */
    public static final double DEFAULT_MULTIPLIER = 0.1;

    private final double multiplier;

    public ProportionalRandomJitterRetryPolicy(RetryPolicy target) {
        this(target, DEFAULT_MULTIPLIER);
    }

    public ProportionalRandomJitterRetryPolicy(RetryPolicy target, Random random) {
        this(target, DEFAULT_MULTIPLIER, random);
    }

    public ProportionalRandomJitterRetryPolicy(RetryPolicy target, double multiplier) {
        super(target);
        this.multiplier = multiplier;
    }

    public ProportionalRandomJitterRetryPolicy(RetryPolicy target, double multiplier, Random random) {
        super(target, random);
        this.multiplier = multiplier;
    }

    @Override
    long addRandomJitter(long initialDelay) {
        final double randomMultiplier = (1 - 2 * random().nextDouble()) * multiplier;
        return (long) (initialDelay * randomMultiplier);
    }
}
