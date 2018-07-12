package me.dong.asyncretry.policy.exception;

/**
 * Created by ethan.kim on 2018. 7. 11..
 */
public class AbortRetryException extends RuntimeException {

    public AbortRetryException() {
    }

    public AbortRetryException(String message) {
        super(message);
    }
}
