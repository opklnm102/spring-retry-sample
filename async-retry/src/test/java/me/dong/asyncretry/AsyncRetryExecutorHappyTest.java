package me.dong.asyncretry;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.notNull;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

/**
 * Created by ethan.kim on 2018. 7. 13..
 */
public class AsyncRetryExecutorHappyTest extends AbstractBaseTestCase {

    @Test
    public void shouldNotRetryIfCompletesAfterFirstExecution() throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock);

        // when :
        executor.doWithRetry(context -> serviceMock.alwaysSucceeds());

        // then :
        verify(schedulerMock).schedule((Runnable) notNull(), eq(0L), eq(TimeUnit.MILLISECONDS));
        verifyNoMoreInteractions(schedulerMock);
    }

    @Test
    public void shouldCallUserTaskOnlyOnceIfItDoesntFail() throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock);

        // when :
        executor.doWithRetry(context -> serviceMock.alwaysSucceeds());

        // then :
        verify(serviceMock).alwaysSucceeds();
    }

    @Test
    public void shouldReturnResultOfFirstSuccessfulCall() throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock);
        given(serviceMock.alwaysSucceeds()).willReturn(42);

        // when :
        final CompletableFuture<Integer> future = executor.getWithRetry(serviceMock::alwaysSucceeds);

        // then :
        assertThat(future.get()).isEqualTo(42);
    }

    @Test
    public void shouldReturnEvenIfNoRetryPolicy() throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock).dontRetry();
        given(serviceMock.alwaysSucceeds()).willReturn(42);

        // when :
        final CompletableFuture<Integer> future = executor.getWithRetry(serviceMock::alwaysSucceeds);

        // then :
        assertThat(future.get()).isEqualTo(42);
    }
}
