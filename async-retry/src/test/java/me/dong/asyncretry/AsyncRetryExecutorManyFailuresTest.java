package me.dong.asyncretry;

import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import me.dong.asyncretry.backoff.FixedIntervalBackoff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.failBecauseExceptionWasNotThrown;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by ethan.kim on 2018. 7. 13..
 */
public class AsyncRetryExecutorManyFailuresTest extends AbstractBaseTestCase {

    @Test
    public void shouldRethrowIfFirstFewExecutionsThrow() throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock).withMaxRetries(2);
        given(serviceMock.sometimesFails()).willThrow(new IllegalStateException(DON_T_PANIC));

        // when :
        final CompletableFuture<String> future = executor.getWithRetry(serviceMock::sometimesFails);

        // then :
        assertThat(future.isCompletedExceptionally()).isTrue();
        try {
            future.get();
            failBecauseExceptionWasNotThrown(IllegalStateException.class);
        } catch (ExecutionException e) {
            assertThat(e.getCause()).isInstanceOf(TooManyRetriesException.class);
            assertThat(((TooManyRetriesException) e.getCause()).getRetries()).isEqualTo(2);

            final Throwable actualCause = e.getCause().getCause();
            assertThat(actualCause).isInstanceOf(IllegalStateException.class);
            assertThat(actualCause.getMessage()).isEqualToIgnoringCase(DON_T_PANIC);
        }
    }

    @Test
    public void shouldRetryAfterManyExceptionsAndReturnValue() throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock);
        given(serviceMock.sometimesFails())
                .willThrow(IllegalStateException.class, IllegalStateException.class, IllegalStateException.class)
                .willReturn("Foo");

        // when :
        final CompletableFuture<String> future = executor.getWithRetry(serviceMock::sometimesFails);

        // then :
        assertThat(future.get()).isEqualTo("Foo");
    }

    @Test
    public void shouldSucceedWhenTheSameNumberOfRetriesAsFailuresAllowed () throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock).withMaxRetries(3);
        given(serviceMock.sometimesFails())
                .willThrow(IllegalStateException.class, IllegalStateException.class, IllegalStateException.class)
                .willReturn("Foo");

        // when :
        final CompletableFuture<String> future = executor.getWithRetry(serviceMock::sometimesFails);

        // then :
        assertThat(future.get()).isEqualTo("Foo");
    }

    @Test
    public void shouldRetryManyTimesIfFirstExecutionsThrowException () throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock);
        given(serviceMock.sometimesFails())
                .willThrow(IllegalStateException.class, IllegalStateException.class, IllegalStateException.class)
                .willReturn("Foo");

        // when :
        executor.getWithRetry(serviceMock::sometimesFails);

        // then :
        verify(serviceMock, times(4)).sometimesFails();
    }

    @Test
    public void shouldScheduleRetryWithDefaultDelay () throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock);
        given(serviceMock.sometimesFails())
                .willThrow(IllegalStateException.class, IllegalStateException.class, IllegalStateException.class)
                .willReturn("Foo");

        // when :
        executor.getWithRetry(serviceMock::sometimesFails);

        // then :
        final InOrder inOrder = Mockito.inOrder(schedulerMock);
        inOrder.verify(schedulerMock).schedule(notNullRunnable(), eq(0L), millis());
        inOrder.verify(schedulerMock, times(3)).schedule(notNullRunnable(),eq(FixedIntervalBackoff.DEFAULT_PERIOD_MILLS), millis());
        inOrder.verifyNoMoreInteractions();
    }

    @Test
    public void shouldPassCorrectRetryCountToEachInvocationInContext () throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock);
        given(serviceMock.calculateSum(0)).willThrow(IllegalStateException.class);
        given(serviceMock.calculateSum(1)).willThrow(IllegalStateException.class);
        given(serviceMock.calculateSum(2)).willThrow(IllegalStateException.class);
        given(serviceMock.calculateSum(3)).willReturn(BigDecimal.ONE);

        // when :
        executor.getWithRetry(ctx -> serviceMock.calculateSum(ctx.getRetryCount()));

        // then :
        final InOrder order = inOrder(serviceMock);
        order.verify(serviceMock).calculateSum(0);
        order.verify(serviceMock).calculateSum(1);
        order.verify(serviceMock).calculateSum(2);
        order.verify(serviceMock).calculateSum(3);
        order.verifyNoMoreInteractions();
    }
}
