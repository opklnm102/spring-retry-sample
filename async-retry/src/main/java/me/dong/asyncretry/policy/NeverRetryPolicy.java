package me.dong.asyncretry.policy;

import me.dong.asyncretry.RetryContext;

/**
 * Created by ethan.kim on 2018. 7. 11..
 */
public class NeverRetryPolicy implements RetryPolicy {

    @Override
    public long delayMillis(RetryContext context) {
        return 0;
    }

    @Override
    public boolean shouldContinue(RetryContext context) {
        return false;
    }
}
