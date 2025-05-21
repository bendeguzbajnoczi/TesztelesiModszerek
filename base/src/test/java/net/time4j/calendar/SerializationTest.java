package net.time4j.calendar;

import net.time4j.calendar.astro.JulianDay;
import net.time4j.history.ChronoHistory;
import net.time4j.history.HistoricEra;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.*;
import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class SerializationTest {

    @Parameterized.Parameters(name = "{index}: roundtrip({0})")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                { "HijriCalendar Umalqura", HijriCalendar.ofUmalqura(1437, 3, 17) },
                { "PersianCalendar",        PersianCalendar.of(1425, 1, 7) },
                { "MinguoCalendar",         MinguoCalendar.of(MinguoEra.ROC, 105, 1, 7) },
                { "CopticCalendar",         CopticCalendar.of(1723, 13, 6) },
                { "EthiopianCalendar",      EthiopianCalendar.of(EthiopianEra.AMETE_MIHRET, 2007, 13, 6) },
                { "EthiopianTime",          EthiopianTime.ofDay(4, 45, 23) },
                { "JulianCalendar AD",      JulianCalendar.of(HistoricEra.AD, 1752, 9, 14) },
                { "JulianCalendar BC",      JulianCalendar.of(HistoricEra.BC, 46, 2, 13) },
                { "ThaiSolarCalendar",      ThaiSolarCalendar.of(ThaiSolarEra.BUDDHIST, 2482, 2, 7) },
                { "JapaneseCalendar Heisei",JapaneseCalendar.ofGregorian(Nengo.HEISEI, 29, 4, 12) },
                { "JapaneseCalendar Showa",  JapaneseCalendar.ofGregorian(Nengo.SHOWA, 64, 1, 7) },
                { "JapaneseCalendar Custom", JapaneseCalendar.of(
                        Nengo.ofRelatedGregorianYear(1857),
                        4,
                        EastAsianMonth.valueOf(5).withLeap(),
                        1) },
                { "IndianCalendar",         IndianCalendar.of(1912, 5, 31) },
                { "JulianDay",              JulianDay.ofEphemerisTime(2451545.0) },
                { "HistoricCalendar",       HistoricCalendar.of(
                        ChronoHistory.of(Locale.UK),
                        HistoricEra.AD, 1603, 3, 24) },
                { "HebrewCalendar",         HebrewCalendar.of(5779, HebrewMonth.ADAR_I, 1) },
                { "HebrewTime",             HebrewTime.ofDay(12, 540) },
                { "ChineseCalendar",        ChineseCalendar.ofNewYear(2018) },
                { "KoreanCalendar",         KoreanCalendar.ofNewYear(2018) },
                { "VietnameseCalendar",     VietnameseCalendar.ofTet(2018) },
                { "JucheCalendar",          JucheCalendar.of(105, 1, 7) },
                { "SexagesimalName",        SexagesimalName.of(11) },
                { "CyclicYear",             CyclicYear.of(11) }
        });
    }

    private final String label;
    private final Object instance;

    public SerializationTest(String label, Object instance) {
        this.label = label;
        this.instance = instance;
    }

    @Test
    public void testSerializeAndDeserialize() throws IOException, ClassNotFoundException {
        byte[] serialized;
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream();
             ObjectOutputStream oos = new ObjectOutputStream(baos)) {
            oos.writeObject(instance);
            serialized = baos.toByteArray();
        }
        try (ByteArrayInputStream bais = new ByteArrayInputStream(serialized);
             ObjectInputStream ois = new ObjectInputStream(bais)) {
            Object restored = ois.readObject();
            assertThat(
                    "Round-trip failed for " + label,
                    restored,
                    is(instance)
            );
        }
    }
}
