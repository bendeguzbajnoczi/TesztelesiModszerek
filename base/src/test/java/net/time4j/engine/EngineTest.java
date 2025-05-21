package net.time4j.engine;

import net.time4j.PlainDate;
import org.junit.Test;

import java.text.DateFormat;
import java.time.format.FormatStyle;
import java.util.Locale;

import static org.junit.Assert.*;

public class EngineTest {

    @Test
    public void testChronoElementGetDisplayName() {
        ChronoElement<Integer> element = PlainDate.YEAR;
        String expected = "year";
        assertEquals( expected,
                element.getDisplayName(Locale.US));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testTimeMetricDefaultReversibleThrows() {
        new DummyMetric().reversible();
    }

    @Test
    public void testChronoUnitIsCalendrical() {
        ChronoUnit oneDay = new DummyUnit(86400.0);
        assertTrue("86400s should be calendrical", oneDay.isCalendrical());

        ChronoUnit twoDays = new DummyUnit(86400.0 * 2);
        assertTrue("twice a day should be calendrical", twoDays.isCalendrical());
    }

    @Test
    public void testGetInt_whenValueAvailable() {
        final DummyElement elem = new DummyElement("ELEM");

        ChronoDisplay display = new ChronoDisplay() {
            @Override public boolean contains(ChronoElement<?> element) {
                return element == elem;
            }
            @Override public <V> V get(ChronoElement<V> element) {
                return (V) Integer.valueOf(42);
            }
            @Override public <V> V getMinimum(ChronoElement<V> element) { throw new UnsupportedOperationException(); }
            @Override public <V> V getMaximum(ChronoElement<V> element) { throw new UnsupportedOperationException(); }
            @Override public boolean hasTimezone() { return false; }
            @Override public net.time4j.tz.TZID getTimezone() { throw new UnsupportedOperationException(); }
        };

        assertEquals(42, display.getInt(elem));
    }

    @Test
    public void testDisplayStyleToThreetenFull() {
        DisplayStyle ds = of(DateFormat.FULL);
        assertEquals(FormatStyle.FULL, ds.toThreeten());
    }

    @Test
    public void testDisplayStyleToThreetenLong() {
        DisplayStyle ds = of(DateFormat.LONG);
        assertEquals(FormatStyle.LONG, ds.toThreeten());
    }








    private static class DummyMetric implements TimeMetric<Object, Object> {
        @Override
        public <T extends TimePoint<? super Object, T>> Object between(T start, T end) {
            return null;
        }
    }

    private static class DummyUnit implements ChronoUnit {
        private final double length;
        DummyUnit(double length) { this.length = length; }
        @Override
        public double getLength() { return length; }
    }

    private static class DummyElement implements ChronoElement<Integer> {
        private final String name;
        DummyElement(String name) { this.name = name; }
        @Override public String name() { return name; }
        @Override public Class<Integer> getType() { return Integer.class; }
        @Override public char getSymbol() { return '\0'; }
        @Override public int compare(net.time4j.engine.ChronoDisplay o1, net.time4j.engine.ChronoDisplay o2) {
            return 0;
        }
        @Override public Integer getDefaultMinimum() { return 0; }
        @Override public Integer getDefaultMaximum() { return 0; }
        @Override public boolean isDateElement() { return false; }
        @Override public boolean isTimeElement() { return false; }
        @Override public boolean isLenient() { return false; }
        @Override public String getDisplayName(java.util.Locale language) { return name; }
    }

    private DisplayStyle of(int styleValue) {
        return () -> styleValue;
    }


}
