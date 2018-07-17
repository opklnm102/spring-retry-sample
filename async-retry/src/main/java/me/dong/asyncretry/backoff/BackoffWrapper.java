package me.dong.asyncretry.backoff;

import java.util.Objects;

/**
 * Created by ethan.kim on 2018. 7. 17..
 */
public abstract class BackoffWrapper implements Backoff {

    protected final Backoff target;

    public BackoffWrapper(Backoff target) {
        this.target = Objects.requireNonNull(target);
    }
}
