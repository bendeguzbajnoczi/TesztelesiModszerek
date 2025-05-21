package net.time4j;

import net.time4j.format.PluralCategory;
import net.time4j.format.TextWidth;
import net.time4j.format.UnitPatternProvider;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Locale;

import static org.junit.Assert.*;

public class FallbackProviderTest {

    private static UnitPatternProvider fpEn;
    private static UnitPatternProvider fpFr;

    @BeforeClass
    public static void setup() {
        fpEn = new UnitPatterns.FallbackProvider();
        fpFr = new UnitPatterns.FallbackProvider();
    }

    @Test
    public void testEnglishWideAndPluralization() {
        assertEquals("{0} year",  fpEn.getYearPattern(Locale.ENGLISH, TextWidth.WIDE, PluralCategory.ONE));
        assertEquals("{0} years", fpEn.getYearPattern(Locale.ENGLISH, TextWidth.WIDE, PluralCategory.OTHER));
    }

    @Test
    public void testEnglishAbbreviatedAndNarrow() {
        assertEquals("{0} yr", fpEn.getYearPattern(Locale.ENGLISH, TextWidth.ABBREVIATED, PluralCategory.ONE));
        assertEquals("{0} yrs", fpEn.getYearPattern(Locale.ENGLISH, TextWidth.SHORT, PluralCategory.OTHER));
        assertEquals("{0}y",   fpEn.getYearPattern(Locale.ENGLISH, TextWidth.NARROW, PluralCategory.ONE));
    }

    @Test
    public void testGenericFallbackNonEnglish() {
        assertEquals("{0} y", fpFr.getYearPattern(Locale.FRENCH, TextWidth.WIDE, PluralCategory.ONE));
        assertEquals("{0} y", fpFr.getYearPattern(Locale.FRENCH, TextWidth.SHORT, PluralCategory.OTHER));
    }

    @Test
    public void testRelativeEnglishFutureAndPast() {
        assertEquals("in {0} days", fpEn.getDayPattern(Locale.ENGLISH, true, PluralCategory.OTHER));
        assertEquals("{0} day ago", fpEn.getDayPattern(Locale.ENGLISH, false, PluralCategory.ONE));
    }

    @Test
    public void testRelativeGenericNonEnglish() {
        assertEquals("+{0} d", fpFr.getDayPattern(Locale.FRENCH, true, PluralCategory.ONE));
        assertEquals("-{0} d", fpFr.getDayPattern(Locale.FRENCH, false, PluralCategory.OTHER));
    }

    @Test
    public void testNowWord() {
        assertEquals("now", fpEn.getNowWord(Locale.ENGLISH));
        assertEquals("now", fpFr.getNowWord(Locale.FRENCH));
    }

    @Test
    public void testListPatternValidSizes() {
        assertEquals("{0}, {1}",           fpEn.getListPattern(Locale.ENGLISH, TextWidth.WIDE, 2));
        assertEquals("{0}, {1}, {2}",      fpEn.getListPattern(Locale.ENGLISH, TextWidth.SHORT, 3));
        assertEquals("{0}, {1}, {2}, {3}", fpEn.getListPattern(Locale.ENGLISH, TextWidth.NARROW, 4));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testListPatternTooSmall() {
        fpEn.getListPattern(Locale.ENGLISH, TextWidth.WIDE, 1);
    }

    @Test
    public void testListPatternTooLarge() {
        String expected = "{0}, {1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}";
        assertEquals(expected, fpEn.getListPattern(Locale.ENGLISH, TextWidth.WIDE, 9));
    }

    @Test
    public void testAllUnitsSupported() {
        for (IsoUnit u : UnitPatterns.UNIT_IDS) {
            if (!Character.isDigit(u.getSymbol())) {
                for (PluralCategory cat : PluralCategory.values()) {
                    String p1 = fpEn.getYearPattern(Locale.ENGLISH, TextWidth.WIDE, cat);
                    assertNotNull(p1);
                    String p2 = fpEn.getDayPattern(Locale.ENGLISH, true, cat);
                    String p3 = fpEn.getDayPattern(Locale.ENGLISH, false, cat);
                    assertNotNull(p2);
                    assertNotNull(p3);
                }
            }
        }
    }
}
