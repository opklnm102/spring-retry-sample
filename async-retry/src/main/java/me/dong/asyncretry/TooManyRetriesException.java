package me.dong.asyncretry;

/**
 * Created by ethan.kim on 2018. 7. 13..
 */
public class TooManyRetriesException extends RuntimeException {

    private final int retries;

    public TooManyRetriesException(int retryCount, Throwable lastCause) {
        super("Too many retries: " + retryCount, lastCause);
        this.retries = retryCount;
    }

    public int getRetries() {
        return retries;
    }
}
