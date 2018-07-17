package me.dong.asyncretry;

import java.math.BigDecimal;

/**
 * Created by ethan.kim on 2018. 7. 13..
 */
public interface FaultyService {

    int alwaysSucceeds();

    String sometimesFails();

    BigDecimal calculateSum(int retry);

    void withFlag(boolean flag);
}
