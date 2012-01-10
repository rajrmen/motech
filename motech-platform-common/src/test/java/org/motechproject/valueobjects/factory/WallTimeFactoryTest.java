package org.motechproject.valueobjects.factory;

import junit.framework.Assert;
import org.junit.Test;
import org.motechproject.valueobjects.WallTime;
import org.motechproject.valueobjects.WallTimeUnit;
import org.motechproject.valueobjects.factory.WallTimeFactory;

public class WallTimeFactoryTest {
    @Test
    public void create() {
    	Assert.assertEquals(wallTime(5, WallTimeUnit.Minute), user("5 minutes"));
    	
    	
    	Assert.assertEquals(wallTime(1, WallTimeUnit.Minute), user("1 minute"));
    	Assert.assertEquals(wallTime(1, WallTimeUnit.Day).inMinutes(), user("1440 minutes").inMinutes());
    	Assert.assertEquals(wallTime(1, WallTimeUnit.Hour).inMinutes(), user("60 minutes").inMinutes());
    	Assert.assertEquals(wallTime(1, WallTimeUnit.Day).inMinutes(), user("24 hours").inMinutes());
        Assert.assertEquals(wallTime(1, WallTimeUnit.Day), user("1 Day"));
        Assert.assertEquals(wallTime(1, WallTimeUnit.Day), user("1 day"));
        Assert.assertEquals(wallTime(1, WallTimeUnit.Day), user(" 1 day "));
        Assert.assertEquals(wallTime(2, WallTimeUnit.Day), user(" 2 days "));
        Assert.assertEquals(wallTime(2, WallTimeUnit.Week), user(" 2 weeks "));
        Assert.assertEquals(null, user(""));
       
    }

    private WallTime user(String userReadableForm) {
        return WallTimeFactory.create(userReadableForm);
    }

    private WallTime wallTime(int value, WallTimeUnit unit) {
        return new WallTime(value, unit);
    }
}
