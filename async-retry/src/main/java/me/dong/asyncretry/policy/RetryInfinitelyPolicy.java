package me.dong.asyncretry.policy;

import me.dong.asyncretry.RetryContext;

/**
 * Created by ethan.kim on 2018. 7. 17..
 */
public class RetryInfinitelyPolicy implements RetryPolicy{

    @Override
    public boolean shouldContinue(RetryContext context) {
        return true;
    }
}
