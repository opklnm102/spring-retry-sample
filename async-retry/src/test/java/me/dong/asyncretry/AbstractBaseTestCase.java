package me.dong.asyncretry;

import org.junit.Before;
import org.mockito.MockitoAnnotations;

/**
 * Created by ethan.kim on 2018. 7. 9..
 */
public class AbstractBaseTestCase {

    @Before
    public void injectMocks() {
        MockitoAnnotations.initMocks(this);
    }
}
