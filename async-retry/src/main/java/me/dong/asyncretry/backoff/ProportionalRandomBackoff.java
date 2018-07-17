package me.dong.asyncretry.backoff;

import java.util.Random;

import me.dong.asyncretry.policy.RetryPolicy;

/**
 * Created by ethan.kim on 2018. 7. 10..
 */
public class ProportionalRandomBackoff extends RandomDelayBackoff {

    /**
     * Randomly up to +/- 10%
     */
    public static final double DEFAULT_MULTIPLIER = 0.1;

    private final double multiplier;

    public ProportionalRandomBackoff(Backoff target) {
        this(target, DEFAULT_MULTIPLIER);
    }

    public ProportionalRandomBackoff(Backoff target, Random random) {
        this(target, DEFAULT_MULTIPLIER, random);
    }

    public ProportionalRandomBackoff(Backoff target, double multiplier) {
        super(target);
        this.multiplier = multiplier;
    }

    public ProportionalRandomBackoff(Backoff target, double multiplier, Random random) {
        super(target, random);
        this.multiplier = multiplier;
    }

    @Override
    long addRandomJitter(long initialDelay) {
        final double randomMultiplier = (1 - 2 * random().nextDouble()) * multiplier;
        return (long) (initialDelay * randomMultiplier);
    }
}
