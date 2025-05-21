package net.time4j.calendar;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class HebrewMonthRefactoredTest {

    @Test
    public void getValue() {
        class MonthValue {
            final HebrewMonth month;
            final int expected;

            MonthValue(HebrewMonth month, int expected) {
                this.month = month;
                this.expected = expected;
            }
        }

        MonthValue[] cases = new MonthValue[] {
                new MonthValue(HebrewMonth.TISHRI, 1),
                new MonthValue(HebrewMonth.HESHVAN, 2),
                new MonthValue(HebrewMonth.KISLEV, 3),
                new MonthValue(HebrewMonth.TEVET, 4),
                new MonthValue(HebrewMonth.SHEVAT, 5),
                new MonthValue(HebrewMonth.ADAR_I, 6),
                new MonthValue(HebrewMonth.ADAR_II, 7),
                new MonthValue(HebrewMonth.NISAN, 8),
                new MonthValue(HebrewMonth.IYAR, 9),
                new MonthValue(HebrewMonth.SIVAN, 10),
                new MonthValue(HebrewMonth.TAMUZ, 11),
                new MonthValue(HebrewMonth.AV, 12),
                new MonthValue(HebrewMonth.ELUL, 13)
        };

        for (MonthValue mv : cases) {
            assertThat("Value of " + mv.month, mv.month.getValue(), is(mv.expected));
        }
    }


    @Test
    public void getCivilValueNormalYear() {
        Object[][] cases = {
                { HebrewMonth.TISHRI, 1 },
                { HebrewMonth.HESHVAN, 2 },
                { HebrewMonth.KISLEV, 3 },
                { HebrewMonth.TEVET, 4 },
                { HebrewMonth.SHEVAT, 5 },
                { HebrewMonth.ADAR_I, 6 },
                { HebrewMonth.ADAR_II, 6 },
                { HebrewMonth.NISAN, 7 },
                { HebrewMonth.IYAR, 8 },
                { HebrewMonth.SIVAN, 9 },
                { HebrewMonth.TAMUZ, 10 },
                { HebrewMonth.AV, 11 },
                { HebrewMonth.ELUL, 12 }
        };

        for (Object[] c : cases) {
            HebrewMonth month = (HebrewMonth) c[0];
            int expected = (int) c[1];
            assertThat("Civil value (non-leap) of " + month, month.getCivilValue(false), is(expected));
        }
    }


    @Test
    public void getCivilValueLeapYear() {
        Object[][] cases = {
                { HebrewMonth.TISHRI, 1 },
                { HebrewMonth.HESHVAN, 2 },
                { HebrewMonth.KISLEV, 3 },
                { HebrewMonth.TEVET, 4 },
                { HebrewMonth.SHEVAT, 5 },
                { HebrewMonth.ADAR_I, 6 },
                { HebrewMonth.ADAR_II, 7 },
                { HebrewMonth.NISAN, 8 },
                { HebrewMonth.IYAR, 9 },
                { HebrewMonth.SIVAN, 10 },
                { HebrewMonth.TAMUZ, 11 },
                { HebrewMonth.AV, 12 },
                { HebrewMonth.ELUL, 13 }
        };

        for (Object[] c : cases) {
            HebrewMonth month = (HebrewMonth) c[0];
            int expected = (int) c[1];
            assertThat("Civil value (leap) of " + month, month.getCivilValue(true), is(expected));
        }
    }


    @Test
    public void valueOfCivilNormalYear() {
        Object[][] cases = {
                { 1, HebrewMonth.TISHRI },
                { 2, HebrewMonth.HESHVAN },
                { 3, HebrewMonth.KISLEV },
                { 4, HebrewMonth.TEVET },
                { 5, HebrewMonth.SHEVAT },
                { 6, HebrewMonth.ADAR_II },
                { 7, HebrewMonth.NISAN },
                { 8, HebrewMonth.IYAR },
                { 9, HebrewMonth.SIVAN },
                { 10, HebrewMonth.TAMUZ },
                { 11, HebrewMonth.AV },
                { 12, HebrewMonth.ELUL }
        };

        for (Object[] c : cases) {
            int input = (int) c[0];
            HebrewMonth expected = (HebrewMonth) c[1];
            assertThat("valueOfCivil(" + input + ", false)", HebrewMonth.valueOfCivil(input, false), is(expected));
        }
    }


    @Test(expected=IllegalArgumentException.class)
    public void valueOfCivilInvalid() {
        HebrewMonth.valueOfCivil(13, false);
    }

    @Test
    public void valueOfCivilLeapYear() {
        Object[][] cases = {
                { 1, HebrewMonth.TISHRI },
                { 2, HebrewMonth.HESHVAN },
                { 3, HebrewMonth.KISLEV },
                { 4, HebrewMonth.TEVET },
                { 5, HebrewMonth.SHEVAT },
                { 6, HebrewMonth.ADAR_I },
                { 7, HebrewMonth.ADAR_II },
                { 8, HebrewMonth.NISAN },
                { 9, HebrewMonth.IYAR },
                { 10, HebrewMonth.SIVAN },
                { 11, HebrewMonth.TAMUZ },
                { 12, HebrewMonth.AV },
                { 13, HebrewMonth.ELUL }
        };

        for (Object[] c : cases) {
            int input = (int) c[0];
            HebrewMonth expected = (HebrewMonth) c[1];
            assertThat("valueOfCivil(" + input + ", true)", HebrewMonth.valueOfCivil(input, true), is(expected));
        }
    }


    @Test
    public void getBiblicalValueNormalYear() {
        Object[][] cases = {
                { HebrewMonth.TISHRI, 7 },
                { HebrewMonth.HESHVAN, 8 },
                { HebrewMonth.KISLEV, 9 },
                { HebrewMonth.TEVET, 10 },
                { HebrewMonth.SHEVAT, 11 },
                { HebrewMonth.ADAR_I, 12 },
                { HebrewMonth.ADAR_II, 12 },
                { HebrewMonth.NISAN, 1 },
                { HebrewMonth.IYAR, 2 },
                { HebrewMonth.SIVAN, 3 },
                { HebrewMonth.TAMUZ, 4 },
                { HebrewMonth.AV, 5 },
                { HebrewMonth.ELUL, 6 }
        };

        for (Object[] c : cases) {
            HebrewMonth month = (HebrewMonth) c[0];
            int expected = (int) c[1];
            assertThat("Biblical value (non-leap) of " + month, month.getBiblicalValue(false), is(expected));
        }
    }


    @Test
    public void getBiblicalValueLeapYear() {
        Object[][] cases = {
                { HebrewMonth.TISHRI, 7 },
                { HebrewMonth.HESHVAN, 8 },
                { HebrewMonth.KISLEV, 9 },
                { HebrewMonth.TEVET, 10 },
                { HebrewMonth.SHEVAT, 11 },
                { HebrewMonth.ADAR_I, 12 },
                { HebrewMonth.ADAR_II, 13 },
                { HebrewMonth.NISAN, 1 },
                { HebrewMonth.IYAR, 2 },
                { HebrewMonth.SIVAN, 3 },
                { HebrewMonth.TAMUZ, 4 },
                { HebrewMonth.AV, 5 },
                { HebrewMonth.ELUL, 6 }
        };

        for (Object[] c : cases) {
            HebrewMonth month = (HebrewMonth) c[0];
            int expected = (int) c[1];
            assertThat("Biblical value (leap) of " + month, month.getBiblicalValue(true), is(expected));
        }
    }


    @Test
    public void valueOfBiblicalNormalYear() {
        Object[][] cases = {
                { 1, HebrewMonth.NISAN },
                { 2, HebrewMonth.IYAR },
                { 3, HebrewMonth.SIVAN },
                { 4, HebrewMonth.TAMUZ },
                { 5, HebrewMonth.AV },
                { 6, HebrewMonth.ELUL },
                { 7, HebrewMonth.TISHRI },
                { 8, HebrewMonth.HESHVAN },
                { 9, HebrewMonth.KISLEV },
                { 10, HebrewMonth.TEVET },
                { 11, HebrewMonth.SHEVAT },
                { 12, HebrewMonth.ADAR_II }
        };

        for (Object[] c : cases) {
            int input = (int) c[0];
            HebrewMonth expected = (HebrewMonth) c[1];
            assertThat("valueOfBiblical(" + input + ", false)", HebrewMonth.valueOfBiblical(input, false), is(expected));
        }
    }


    @Test(expected=IllegalArgumentException.class)
    public void valueOfBiblicalInvalid() {
        HebrewMonth.valueOfBiblical(13, false);
    }

    @Test
    public void valueOfBiblicalLeapYear() {
        Object[][] cases = {
                { 1, HebrewMonth.NISAN },
                { 2, HebrewMonth.IYAR },
                { 3, HebrewMonth.SIVAN },
                { 4, HebrewMonth.TAMUZ },
                { 5, HebrewMonth.AV },
                { 6, HebrewMonth.ELUL },
                { 7, HebrewMonth.TISHRI },
                { 8, HebrewMonth.HESHVAN },
                { 9, HebrewMonth.KISLEV },
                { 10, HebrewMonth.TEVET },
                { 11, HebrewMonth.SHEVAT },
                { 12, HebrewMonth.ADAR_I },
                { 13, HebrewMonth.ADAR_II }
        };

        for (Object[] c : cases) {
            int input = (int) c[0];
            HebrewMonth expected = (HebrewMonth) c[1];
            assertThat("valueOfBiblical(" + input + ", true)", HebrewMonth.valueOfBiblical(input, true), is(expected));
        }
    }


    @Test
    public void getDisplayName() {
        assertThat(
            HebrewMonth.ADAR_I.getDisplayName(Locale.ROOT, false),
            is("Adar I"));
        assertThat(
            HebrewMonth.ADAR_I.getDisplayName(Locale.ROOT, true),
            is("Adar I"));
        assertThat(
            HebrewMonth.ADAR_II.getDisplayName(Locale.ROOT, false),
            is("Adar"));
        assertThat(
            HebrewMonth.ADAR_II.getDisplayName(Locale.ROOT, true),
            is("Adar II"));
    }

    @Test
    public void isValid() {
        assertThat(
            HebrewCalendar.of(5778, HebrewMonth.NISAN, 30).isValid(HebrewCalendar.MONTH_OF_YEAR, HebrewMonth.ADAR_I),
            is(false));
        assertThat(
            HebrewCalendar.of(5779, HebrewMonth.NISAN, 30).isValid(HebrewCalendar.MONTH_OF_YEAR, HebrewMonth.ADAR_I),
            is(true));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withAdarIWhenNormalYear() {
        HebrewCalendar.of(5778, HebrewMonth.NISAN, 30).with(HebrewCalendar.MONTH_OF_YEAR, HebrewMonth.ADAR_I);
    }

    @Test
    public void withAdarIWhenLeapYear() {
        assertThat(
            HebrewCalendar.of(5779, HebrewMonth.NISAN, 30).with(HebrewCalendar.MONTH_OF_YEAR, HebrewMonth.ADAR_I),
            is(HebrewCalendar.of(5779, HebrewMonth.ADAR_I, 30)));
    }

}