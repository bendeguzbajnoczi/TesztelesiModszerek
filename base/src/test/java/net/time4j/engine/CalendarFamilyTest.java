package net.time4j.engine;

import static org.junit.Assert.*;

import java.io.ObjectStreamException;
import java.util.Locale;
import java.util.Map;

import net.time4j.calendar.HistoricCalendar;
import net.time4j.history.ChronoHistory;
import net.time4j.engine.CalendarFamily.CalendarTimeLine;
import org.junit.Before;
import org.junit.Test;

public class CalendarFamilyTest {

    private CalendarFamily<HistoricCalendar> family;
    private String variantName;
    private VariantSource variantSource;
    private CalendarTimeLine<HistoricCalendar> timelineByString;
    private CalendarTimeLine<HistoricCalendar> timelineBySource;

    @Before
    public void setUp() {
        family = HistoricCalendar.family();

        variantSource = ChronoHistory.ofSweden();
        variantName   = variantSource.getVariant();

        timelineByString = (CalendarTimeLine<HistoricCalendar>) family.getTimeLine(variantName);
        timelineBySource = (CalendarTimeLine<HistoricCalendar>) family.getTimeLine(variantSource);
    }

    @Test
    public void testHasCalendarSystem() {
        assertTrue("CalendarFamily should report hasCalendarSystem()", family.hasCalendarSystem());
    }

    @Test(expected = ChronoException.class)
    public void testGetCalendarSystemNoArgThrows() {
        family.getCalendarSystem();
    }

    @Test
    public void testGetCalendarSystemByString() {
        CalendarSystem<HistoricCalendar> cs = family.getCalendarSystem(variantName);
        assertNotNull(cs);
    }

    @Test
    public void testGetCalendarSystemBySource() {
        CalendarSystem<HistoricCalendar> cs = family.getCalendarSystem(variantSource);
        assertNotNull(cs);
    }

    // ---- CalendarTimeLine tests ----

    @Test
    public void testStepForwardAndBackwards() {
        CalendarSystem<HistoricCalendar> cs = family.getCalendarSystem(variantSource);
        HistoricCalendar min = cs.transform(cs.getMinimumSinceUTC());
        HistoricCalendar next = timelineBySource.stepForward(min);

        assertNotNull("Should step forward from minimum", next);
        assertEquals("Stepping backwards returns to original",
                min, timelineBySource.stepBackwards(next));
    }

    @Test
    public void testStepForwardAtMaximum() {
        CalendarSystem<HistoricCalendar> cs = family.getCalendarSystem(variantName);
        HistoricCalendar max = cs.transform(cs.getMaximumSinceUTC());
        assertNull("Step forward at maximum must return null",
                timelineByString.stepForward(max));
    }

    @Test
    public void testStepBackwardsAtMinimum() {
        CalendarSystem<HistoricCalendar> cs = family.getCalendarSystem(variantName);
        HistoricCalendar min = cs.transform(cs.getMinimumSinceUTC());
        assertNull("Step backwards at minimum must return null",
                timelineByString.stepBackwards(min));
    }

    @Test
    public void testIsCalendricalAlwaysTrue() {
        assertTrue("CalendarTimeLine.isCalendrical() always true",
                timelineByString.isCalendrical());
    }

    @Test
    public void testCompare() {
        CalendarSystem<HistoricCalendar> cs = family.getCalendarSystem(variantName);
        HistoricCalendar d1 = cs.transform(cs.getMinimumSinceUTC());
        HistoricCalendar d2 = cs.transform(cs.getMinimumSinceUTC() + 10);
        assertTrue("compare(d1,d2) < 0", timelineByString.compare(d1, d2) < 0);
        assertTrue("compare(d2,d1) > 0", timelineByString.compare(d2, d1) > 0);
        assertEquals("compare(d1,d1) == 0", 0, timelineByString.compare(d1, d1));
    }

    @Test
    public void testGetMinimumMaximum() {
        CalendarSystem<HistoricCalendar> cs = family.getCalendarSystem(variantSource);
        HistoricCalendar expectedMin = cs.transform(cs.getMinimumSinceUTC());
        HistoricCalendar expectedMax = cs.transform(cs.getMaximumSinceUTC());

        assertEquals("getMinimum()", expectedMin, timelineBySource.getMinimum());
        assertEquals("getMaximum()", expectedMax, timelineBySource.getMaximum());
    }
}
