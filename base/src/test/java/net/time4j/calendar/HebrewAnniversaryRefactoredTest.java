package net.time4j.calendar;

import net.time4j.PlainDate;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class HebrewAnniversaryRefactoredTest {

    @Test
    public void barMitzvah() {
        HebrewCalendar birth = HebrewCalendar.of(5776, HebrewMonth.ADAR_I, 30);
        assertThat(
                birth.barMitzvah(),
                is(HebrewCalendar.of(5789, HebrewMonth.NISAN, 1)));
    }

    @Test
    public void batMitzvah() {
        HebrewCalendar birth = HebrewCalendar.of(5776, HebrewMonth.ADAR_II, 29);
        assertThat(
                birth.batMitzvah(),
                is(HebrewCalendar.of(5788, HebrewMonth.ADAR_II, 29)));
    }

    @Test
    public void yahrzeitHeshvan30() {
        HebrewCalendar death = HebrewCalendar.of(5776, HebrewMonth.HESHVAN, 30);
        PlainDate gregorian = PlainDate.of(2015, 11, 12);

        // érvényesség ellenőrzése
        assertThat(HebrewCalendar.isValid(5777, HebrewMonth.HESHVAN, 30), is(false));

        // előzetes max-check ekvivalent expected5778 és expected5779 esetén
        HebrewCalendar expected5778 = HebrewCalendar.of(5778, HebrewMonth.HESHVAN, 29);
        assertThat(expected5778.getMaximum(HebrewCalendar.DAY_OF_MONTH), is(29));
        HebrewCalendar expected5779 = HebrewCalendar.of(5779, HebrewMonth.HESHVAN, 30);
        assertThat(expected5779.getMaximum(HebrewCalendar.DAY_OF_MONTH), is(30));

        // Teszteset-adatok kiszervezése
        class YahrzeitCase {
            final int        year;
            final HebrewMonth month;
            final int        day;
            final boolean    useDeath;
            final boolean    fromExpected5779;
            final boolean    includeGregorian;

            YahrzeitCase(int year, HebrewMonth month, int day, boolean useDeath, boolean fromExpected5779, boolean includeGregorian) {
                this.year              = year;
                this.month             = month;
                this.day               = day;
                this.useDeath          = useDeath;
                this.fromExpected5779  = fromExpected5779;
                this.includeGregorian  = includeGregorian;
            }
        }

        YahrzeitCase[] cases = new YahrzeitCase[] {
                new YahrzeitCase(5778, HebrewMonth.HESHVAN, 29, true,  false, true),
                new YahrzeitCase(5779, HebrewMonth.HESHVAN, 30, true,  false, true),
                new YahrzeitCase(5780, HebrewMonth.HESHVAN, 30, true,  false, false),
                new YahrzeitCase(5781, HebrewMonth.HESHVAN, 29, true,  false, false),
                new YahrzeitCase(5781, HebrewMonth.KISLEV,  1, false, true,  false),
                new YahrzeitCase(5782, HebrewMonth.HESHVAN, 29, true,  false, false),
                new YahrzeitCase(5783, HebrewMonth.HESHVAN, 30, true,  false, false),
                new YahrzeitCase(5784, HebrewMonth.HESHVAN, 29, true,  false, false),
                new YahrzeitCase(5785, HebrewMonth.HESHVAN, 30, true,  false, false),
                new YahrzeitCase(5786, HebrewMonth.HESHVAN, 29, true,  false, false),
                new YahrzeitCase(5787, HebrewMonth.HESHVAN, 30, true,  false, false)
        };

        for (YahrzeitCase c : cases) {
            HebrewCalendar expected = HebrewCalendar.of(c.year, c.month, c.day);

            if (c.fromExpected5779) {
                assertThat("expected5779.get YAHRZEIT for " + c.year,
                        expected5779.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(c.year)),
                        is(expected));
            } else if (c.useDeath) {
                assertThat("death.get YAHRZEIT for " + c.year,
                        death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(c.year)),
                        is(expected));
            }

            if (c.includeGregorian) {
                assertThat("gregorian.get YAHRZEIT for " + c.year,
                        gregorian.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(c.year)),
                        is(expected));
            }
        }
    }

    @Test
    public void yahrzeitKislev30() {
        HebrewCalendar death = HebrewCalendar.of(5776, HebrewMonth.KISLEV, 30);
        PlainDate gregorian = PlainDate.of(2015, 12, 12);

        HebrewCalendar expected5778 = HebrewCalendar.of(5778, HebrewMonth.KISLEV, 30);
        assertThat(expected5778.getMaximum(HebrewCalendar.DAY_OF_MONTH), is(30));
        HebrewCalendar expected5779 = HebrewCalendar.of(5779, HebrewMonth.KISLEV, 30);
        assertThat(expected5779.getMaximum(HebrewCalendar.DAY_OF_MONTH), is(30));

        class YahrzeitCase {
            final int year;
            final HebrewMonth month;
            final int day;
            final boolean useDeath;
            final boolean fromExpected5779;
            final boolean includeGregorian;

            YahrzeitCase(int year, HebrewMonth month, int day, boolean useDeath, boolean fromExpected5779, boolean includeGregorian) {
                this.year = year;
                this.month = month;
                this.day = day;
                this.useDeath = useDeath;
                this.fromExpected5779 = fromExpected5779;
                this.includeGregorian = includeGregorian;
            }
        }

        YahrzeitCase[] cases = new YahrzeitCase[] {
                new YahrzeitCase(5778, HebrewMonth.KISLEV, 30, true, false, true),
                new YahrzeitCase(5779, HebrewMonth.KISLEV, 30, true, false, true),
                new YahrzeitCase(5780, HebrewMonth.KISLEV, 30, true, false, false),
                new YahrzeitCase(5781, HebrewMonth.KISLEV, 29, true, false, false),
                new YahrzeitCase(5781, HebrewMonth.TEVET, 1, false, true, false),
                new YahrzeitCase(5782, HebrewMonth.KISLEV, 30, true, false, false),
                new YahrzeitCase(5783, HebrewMonth.KISLEV, 30, true, false, false),
                new YahrzeitCase(5784, HebrewMonth.KISLEV, 29, true, false, false),
                new YahrzeitCase(5785, HebrewMonth.KISLEV, 30, true, false, false),
                new YahrzeitCase(5786, HebrewMonth.KISLEV, 30, true, false, false),
                new YahrzeitCase(5787, HebrewMonth.KISLEV, 30, true, false, false)
        };

        for (YahrzeitCase c : cases) {
            HebrewCalendar expected = HebrewCalendar.of(c.year, c.month, c.day);

            if (c.fromExpected5779) {
                assertThat("expected5779.get YAHRZEIT for " + c.year,
                        expected5779.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(c.year)),
                        is(expected));
            } else if (c.useDeath) {
                assertThat("death.get YAHRZEIT for " + c.year,
                        death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(c.year)),
                        is(expected));
            }

            if (c.includeGregorian) {
                assertThat("gregorian.get YAHRZEIT for " + c.year,
                        gregorian.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(c.year)),
                        is(expected));
            }
        }
    }

    @Test
    public void yahrzeitAdarII() {
        HebrewCalendar death = HebrewCalendar.of(5776, HebrewMonth.ADAR_II, 15);
        assertThat(
                death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5777)),
                is(HebrewCalendar.of(5777, HebrewMonth.ADAR_II, 15)));
        assertThat(HebrewCalendar.isLeapYear(5777), is(false));
        assertThat(
                death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5787)),
                is(HebrewCalendar.of(5787, HebrewMonth.ADAR_II, 15)));
        assertThat(HebrewCalendar.isLeapYear(5787), is(true));
    }

    @Test
    public void yahrzeitAdar30() {
        HebrewCalendar death = HebrewCalendar.of(5776, HebrewMonth.ADAR_I, 30);
        assertThat(
                death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5777)),
                is(HebrewCalendar.of(5777, HebrewMonth.SHEVAT, 30))); // disagree with calculator on www.chabad.org
        assertThat(HebrewCalendar.isLeapYear(5777), is(false));
        assertThat(
                death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5787)),
                is(HebrewCalendar.of(5787, HebrewMonth.ADAR_I, 30)));
        assertThat(HebrewCalendar.isLeapYear(5787), is(true));
    }

    @Test
    public void yahrzeitNormal() {
        HebrewCalendar death = HebrewCalendar.of(5776, HebrewMonth.ELUL, 29);
        assertThat(
                death.get(HebrewAnniversary.YAHRZEIT.inHebrewYear(5777)),
                is(HebrewCalendar.of(5777, HebrewMonth.ELUL, 29)));
    }

    @Test
    public void inGregorianYear() {
        HebrewCalendar birth = HebrewCalendar.of(5777, HebrewMonth.TEVET, 4);
        HebrewCalendar barMitzvah = HebrewCalendar.of(5790, HebrewMonth.TEVET, 4);
        assertThat(
                birth.transform(PlainDate.axis()),
                is(PlainDate.of(2017, 1, 2)));
        assertThat(
                birth.barMitzvah(),
                is(barMitzvah));
        assertThat(
                birth.get(HebrewAnniversary.BIRTHDAY.inGregorianYear(2029)),
                is(Collections.singletonList(barMitzvah.transform(PlainDate.axis()))));
        assertThat(
                birth.get(HebrewAnniversary.BIRTHDAY.inGregorianYear(2028)),
                is(Arrays.asList(PlainDate.of(2028, 1, 3), PlainDate.of(2028, 12, 22))));
    }

}