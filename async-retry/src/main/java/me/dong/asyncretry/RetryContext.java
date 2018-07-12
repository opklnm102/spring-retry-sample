package me.dong.asyncretry;

/**
 * Created by ethan.kim on 2018. 7. 9..
 */
public interface RetryContext {

    boolean willRetry();

    int getRetryCount();

    Throwable getLastThrowable();
}
