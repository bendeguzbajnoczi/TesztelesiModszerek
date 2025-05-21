package net.time4j.calendar;

import net.time4j.engine.CalendarSystem;
import net.time4j.history.ChronoHistory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;

@RunWith(Parameterized.class)
public class OutOfRangeTest {

    private final String name;
    private final CalendarSystem<?> calsys;
    private final long value;

    public OutOfRangeTest(String name, CalendarSystem<?> calsys, long value) {
        this.name = name;
        this.calsys = calsys;
        this.value = value;
    }

    @Parameterized.Parameters(name = "{0} throws on {2}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // chinese
                { "Chinese.MIN",     ChineseCalendar.axis().getCalendarSystem(), Long.MIN_VALUE },
                { "Chinese.MAX",     ChineseCalendar.axis().getCalendarSystem(), Long.MAX_VALUE },
                // coptic
                { "Coptic.MIN",      CopticCalendar.axis().getCalendarSystem(), Long.MIN_VALUE },
                { "Coptic.MAX",      CopticCalendar.axis().getCalendarSystem(), Long.MAX_VALUE },
                // ethiopian
                { "Ethiopian.MIN",   EthiopianCalendar.axis().getCalendarSystem(), Long.MIN_VALUE },
                { "Ethiopian.MAX",   EthiopianCalendar.axis().getCalendarSystem(), Long.MAX_VALUE },
                // persian
                { "Persian.MIN",     PersianCalendar.axis().getCalendarSystem(), Long.MIN_VALUE },
                { "Persian.MAX",     PersianCalendar.axis().getCalendarSystem(),                  Long.MAX_VALUE },
                // thai solar
                { "ThaiSolar.MIN",   ThaiSolarCalendar.axis().getCalendarSystem(),                Long.MIN_VALUE },
                { "ThaiSolar.MAX",   ThaiSolarCalendar.axis().getCalendarSystem(),                Long.MAX_VALUE },
                // hijri (civil)
                { "HijriCivil.MIN",  HijriCalendar.family().getCalendarSystem(HijriAlgorithm.WEST_ISLAMIC_CIVIL), Long.MIN_VALUE },
                { "HijriCivil.MAX",  HijriCalendar.family().getCalendarSystem(HijriAlgorithm.WEST_ISLAMIC_CIVIL), Long.MAX_VALUE },
                // hijri (umalqura)
                { "HijriUmalqura.MIN", HijriCalendar.family().getCalendarSystem(HijriCalendar.VARIANT_UMALQURA), Long.MIN_VALUE },
                { "HijriUmalqura.MAX", HijriCalendar.family().getCalendarSystem(HijriCalendar.VARIANT_UMALQURA), Long.MAX_VALUE },
                // korean
                { "Korean.MIN",      KoreanCalendar.axis().getCalendarSystem(),                  Long.MIN_VALUE },
                { "Korean.MAX",      KoreanCalendar.axis().getCalendarSystem(),                  Long.MAX_VALUE },
                // vietnamese
                { "Vietnamese.MIN",  VietnameseCalendar.axis().getCalendarSystem(),              Long.MIN_VALUE },
                { "Vietnamese.MAX",  VietnameseCalendar.axis().getCalendarSystem(),              Long.MAX_VALUE },
                // minguo
                { "Minguo.MIN",      MinguoCalendar.axis().getCalendarSystem(),                  Long.MIN_VALUE },
                { "Minguo.MAX",      MinguoCalendar.axis().getCalendarSystem(),                  Long.MAX_VALUE },
                // japanese
                { "Japanese.MIN",    JapaneseCalendar.axis().getCalendarSystem(),                Long.MIN_VALUE },
                { "Japanese.MAX",    JapaneseCalendar.axis().getCalendarSystem(),                Long.MAX_VALUE },
                // hebrew
                { "Hebrew.MIN",      HebrewCalendar.axis().getCalendarSystem(),                  Long.MIN_VALUE },
                { "Hebrew.MAX",      HebrewCalendar.axis().getCalendarSystem(),                  Long.MAX_VALUE },
                // indian
                { "Indian.MIN",      IndianCalendar.axis().getCalendarSystem(),                  Long.MIN_VALUE },
                { "Indian.MAX",      IndianCalendar.axis().getCalendarSystem(),                  Long.MAX_VALUE },
                // julian
                { "Julian.MIN",      JulianCalendar.axis().getCalendarSystem(),                  Long.MIN_VALUE },
                { "Julian.MAX",      JulianCalendar.axis().getCalendarSystem(),                  Long.MAX_VALUE },
                // juche
                { "Juche.MIN",       JucheCalendar.axis().getCalendarSystem(),                   Long.MIN_VALUE },
                { "Juche.MAX",       JucheCalendar.axis().getCalendarSystem(),                   Long.MAX_VALUE },
                // sweden (historic)
                { "Sweden.MIN",      HistoricCalendar.family().getCalendarSystem(ChronoHistory.ofSweden()), Long.MIN_VALUE },
                { "Sweden.MAX",      HistoricCalendar.family().getCalendarSystem(ChronoHistory.ofSweden()), Long.MAX_VALUE },
        });
    }

    @Test(expected = IllegalArgumentException.class)
    public void transformOutOfRange() {
        calsys.transform(value);
    }
}
