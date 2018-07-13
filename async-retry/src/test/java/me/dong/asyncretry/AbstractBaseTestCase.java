package me.dong.asyncretry;

import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

/**
 * Created by ethan.kim on 2018. 7. 9..
 */
public class AbstractBaseTestCase {

    @Mock
    protected ScheduledExecutorService schedulerMock;

    @Mock
    protected FaultyService serviceMock;

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
        setupMocks();
    }

    private void setupMocks() {
        given(schedulerMock.schedule((Runnable) notNull(), anyLong(), eq(TimeUnit.MILLISECONDS)))
                .willAnswer(invocation -> {
                    ((Runnable) invocation.getArguments()[0]).run();
                    return null;
                });
    }

    protected Runnable notNullRunnable() {
        return (Runnable) notNull();
    }

    protected TimeUnit millis() {
        return eq(TimeUnit.MILLISECONDS);
    }
}
