package me.dong.asyncretry;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import me.dong.asyncretry.policy.FixedIntervalRetryPolicy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.assertj.core.api.Assertions.in;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by ethan.kim on 2018. 7. 17..
 */
public class AsyncRetryExecutorManualAbortTest extends AbstractBaseTestCase {

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
    public void shouldSucceedWhenOnlyOneRetryAllowed() throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock).withMaxRetries(1);
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
        executor.getWithRetry(serviceMock::sometimesFails);

        // then :
        verify(serviceMock, times(2)).sometimesFails();
    }

    @Test
    public void shouldScheduleRetryWithDefaultDelay() throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock);
        given(serviceMock.sometimesFails())
                .willThrow(IllegalStateException.class)
                .willReturn("Foo");

        // when :
        executor.getWithRetry(serviceMock::sometimesFails);

        // then :
        final InOrder inOrder = Mockito.inOrder(schedulerMock);
        inOrder.verify(schedulerMock).schedule(notNullRunnable(), eq(0L), millis());
        inOrder.verify(schedulerMock).schedule(notNullRunnable(), eq(FixedIntervalRetryPolicy.DEFAULT_PERIOD_MILLS), millis());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void shouldPassCorrectRetryCountToEachInvocationInContext () throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock);
        given(serviceMock.calculateSum(0)).willThrow(IllegalStateException.class);
        given(serviceMock.calculateSum(1)).willReturn(BigDecimal.ONE);

        // when :
        executor.getWithRetry(context -> serviceMock.calculateSum(context.getRetryCount()));

        // then :
        final InOrder inOrder = Mockito.inOrder(serviceMock);
        inOrder.verify(serviceMock).calculateSum(0);
        inOrder.verify(serviceMock).calculateSum(1);
        inOrder.verifyNoMoreInteractions();
    }
}
