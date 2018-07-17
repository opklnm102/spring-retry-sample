package me.dong.asyncretry;

import org.junit.Test;
import org.mockito.InOrder;

import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.Mockito.*;

/**
 * Created by ethan.kim on 2018. 7. 17..
 */
public class AsyncRetryContextTest extends AbstractBaseTestCase {

    @Test
    public void shouldNotRetryIfRetriesForbidden() throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock).dontRetry();

        // when :
        executor.doWithRetry(context -> serviceMock.withFlag(context.willRetry()));

        // then :
        verify(serviceMock).withFlag(false);
    }

    @Test
    public void shouldSayItWillRetryIfUnlimitedNumberOfRetries() throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock);

        // when :
        executor.doWithRetry(context -> serviceMock.withFlag(context.willRetry()));

        // then :
        verify(serviceMock).withFlag(true);
    }

    @Test
    public void shouldSayItWillRetryForFirstFewCases() throws Exception {
        // given :
        final RetryExecutor executor = new AsyncRetryExecutor(schedulerMock).withMaxRetries(2);
        doThrow(IllegalStateException.class).when(serviceMock).withFlag(anyBoolean());

        // when :
        executor.doWithRetry(context -> serviceMock.withFlag(context.willRetry()));

        // then :
        final InOrder order = inOrder(serviceMock);
        order.verify(serviceMock, times(2)).withFlag(true);
        order.verify(serviceMock).withFlag(false);
        order.verifyNoMoreInteractions();
    }
}
