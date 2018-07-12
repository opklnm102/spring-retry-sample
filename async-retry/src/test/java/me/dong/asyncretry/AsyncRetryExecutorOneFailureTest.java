package me.dong.asyncretry;

import org.junit.Test;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by ethan.kim on 2018. 7. 13..
 */
public class AsyncRetryExecutorOneFailureTest extends AbstractBaseTestCase {

    public static final String DON_T_PANIC = "Don't panic!";

    @Test
    public void shouldRethrowIfFirstExecutionThrowsAnExceptionAndNoRetry() throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock).dontRetry();
        given(serviceMock.sometimesFails())
                .willThrow(new IllegalStateException(DON_T_PANIC));

        // when :
        final CompletableFuture<String> future = executor.getWithRetry(serviceMock::sometimesFails);

        // then :
        assertThat(future.isCompletedExceptionally()).isTrue();
        try {
            future.get();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (ExecutionException e) {
            assertThat(e.getCause()).isInstanceOf(TooManyRetriesException.class);
            assertThat(((TooManyRetriesException) e.getCause()).getRetries()).isEqualTo(0);

            final Throwable actualCause = e.getCause().getCause();
            assertThat(actualCause).isInstanceOf(IllegalStateException.class);
            assertThat(actualCause.getMessage()).isEqualToIgnoringCase(DON_T_PANIC);
        }
    }

    @Test
    public void shouldRetryAfterOneExceptionAndReturnValue() throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock);
        given(serviceMock.sometimesFails())
                .willThrow(IllegalStateException.class)
                .willReturn("Foo");

        // when :
        final CompletableFuture<String> future = executor.getWithRetry(serviceMock::sometimesFails);

        // then :
        assertThat(future.get()).isEqualTo("Foo");
    }

    @Test
    public void shouldRetryOnceIfFirstExecutionThrowsException() throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock);
        given(serviceMock.sometimesFails())
                .willThrow(IllegalStateException.class)
                .willReturn("Foo");

        // when :
        final CompletableFuture<String> future = executor.getWithRetry(serviceMock::sometimesFails);

        // then :
        verify(serviceMock, times(2)).sometimesFails();
    }
}
