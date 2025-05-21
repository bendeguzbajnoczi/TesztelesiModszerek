package net.time4j.engine;

import net.time4j.PlainDate;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.junit.Assert.*;

public class EpochDaysTest {

    private static ElementRule<PlainDate,Long> unixRule;

    @BeforeClass
    public static void setUpOnce() {
        CalendarSystem<PlainDate> calsys = PlainDate.axis().getCalendarSystem();
        unixRule = EpochDays.UNIX.derive(calsys);
    }

    @Test
    public void testCompare() {
        PlainDate d1 = PlainDate.of(1970,1,1);
        PlainDate d2 = PlainDate.of(1970,2,1);
        int cmp = EpochDays.UNIX.compare(d1, d2);
        assertTrue("d1 < d2 in UNIX days", cmp < 0);
        assertEquals("d1 == d1", 0, EpochDays.UNIX.compare(d1,d1));
        assertTrue("d2 > d1", EpochDays.UNIX.compare(d2, d1) > 0);
    }

    @Test
    public void testIsTimeElement() {
        for (EpochDays e : EpochDays.values()) {
            assertFalse(e.name(), e.isTimeElement());
        }
    }

    @Test
    public void testGetMinimumAndMaximum() {
        PlainDate today = PlainDate.of(2025,5,20);
        Long min = unixRule.getMinimum(today);
        Long max = unixRule.getMaximum(today);
        long defaultMin = EpochDays.UNIX.getDefaultMinimum();
        long defaultMax = EpochDays.UNIX.getDefaultMaximum();
        assertEquals(defaultMin, min.longValue());
        assertEquals(defaultMax, max.longValue());
    }

    @Test
    public void testIsValid() {
        PlainDate today = PlainDate.of(2025,5,20);
        long epochDay = today.getDaysSinceEpochUTC();
        long unixDays = EpochDays.UTC.transform(epochDay, EpochDays.UNIX);
        assertTrue("rule.isValid for in-range", unixRule.isValid(today, unixDays));

        assertFalse("too small", unixRule.isValid(today, defaultLong(defaultLong(EpochDays.UNIX.getDefaultMinimum()) - 10)));
        assertFalse("null not valid", unixRule.isValid(today, null));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testWithValueNullThrows() {
        unixRule.withValue(PlainDate.of(2000,1,1), null, false);
    }


    @Test
    public void testChildAtFloorAndCeiling() {
        assertNull("getChildAtFloor", unixRule.getChildAtFloor(PlainDate.of(2025,5,20)));
        assertNull("getChildAtCeiling", unixRule.getChildAtCeiling(PlainDate.of(2025,5,20)));
    }

    private static Long defaultLong(long v) {
        return v;
    }
}
