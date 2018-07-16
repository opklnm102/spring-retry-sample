package me.dong.asyncretry;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import me.dong.asyncretry.policy.exception.AbortRetryException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Created by ethan.kim on 2018. 7. 13..
 */
public class AsyncRetryExecutorOneFailureTest extends AbstractBaseTestCase {

    @Test
    public void shouldNotRetryIfAbortThrown() throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock);
        given(serviceMock.sometimesFails())
                .willThrow(AbortRetryException.class);

        // when :
        executor.getWithRetry(serviceMock::sometimesFails);

        // then :
        verify(serviceMock).sometimesFails();
    }

    @Test
    public void shouldRethrowAbortExceptionIfFirstIterationThrownIt() throws Exception {
        //given
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock);
        given(serviceMock.sometimesFails()).
                willThrow(AbortRetryException.class);

        //when
        final CompletableFuture<String> future = executor.getWithRetry(serviceMock::sometimesFails);

        //then
        assertThat(future.isCompletedExceptionally()).isTrue();
        try {
            future.get();
            failBecauseExceptionWasNotThrown(ExecutionException.class);
        } catch (ExecutionException e) {
            assertThat(e.getCause()).isInstanceOf(AbortRetryException.class);
        }
    }

    @Test
    public void shouldRethrowLastThrownExceptionWhenAbortedInSubsequentIteration() throws Exception {
        //given
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock);
        given(serviceMock.sometimesFails()).
                willThrow(
                        new IllegalArgumentException("First"),
                        new IllegalArgumentException("Second"),
                        new AbortRetryException()
                );

        //when
        final CompletableFuture<String> future = executor.getWithRetry(serviceMock::sometimesFails);

        //then
        assertThat(future.isCompletedExceptionally()).isTrue();
        try {
            future.get();
            failBecauseExceptionWasNotThrown(ExecutionException.class);
        } catch (ExecutionException e) {
            assertThat(e.getCause()).isInstanceOf(IllegalArgumentException.class);
            assertThat(e.getCause().getMessage()).isEqualTo("Second");
        }
    }
}
