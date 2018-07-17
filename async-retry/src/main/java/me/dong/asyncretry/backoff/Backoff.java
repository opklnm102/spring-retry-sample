package me.dong.asyncretry.backoff;

import me.dong.asyncretry.RetryContext;

/**
 * Created by ethan.kim on 2018. 7. 17..
 */
public interface Backoff {

    public static final Backoff DEFAULT = new FixedIntervalBackoff();

    long delayMillis(RetryContext context);

    default Backoff withUniformJitter() {
        return new UniformRandomBackoff(this);
    }

    default Backoff withUniformJitter(long range) {
        return new UniformRandomBackoff(this, range);
    }

    default Backoff withProportionalJitter() {
        return new ProportionalRandomBackoff(this);
    }

    default Backoff withProportionalJitter(double multiplier) {
        return new ProportionalRandomBackoff(this, multiplier);
    }

    default Backoff withMinDelay(long minDelayMillis) {
        return new BoundedMinBackoff(this, minDelayMillis);
    }

    default Backoff withMaxDelay(long maxDelayMillis) {
        return new BoundedMaxBackoff(this, maxDelayMillis);
    }
}
