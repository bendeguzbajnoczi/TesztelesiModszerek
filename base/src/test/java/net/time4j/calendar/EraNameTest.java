package net.time4j.calendar;

import net.time4j.format.TextWidth;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Arrays;
import java.util.Collection;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;


@RunWith(Parameterized.class)
public class EraNameTest {

    @Parameterized.Parameters(name = "{index}: {0}.getDisplayName({1}{2}) => \"{3}\"")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
                // HebrewEra
                { HebrewEra.ANNO_MUNDI, Locale.ENGLISH, TextWidth.WIDE,  "AM" },
                { HebrewEra.ANNO_MUNDI, Locale.ENGLISH, TextWidth.SHORT, "AM" },
                // HijriEra
                { HijriEra.ANNO_HEGIRAE, Locale.ENGLISH, TextWidth.WIDE,  "AH" },
                { HijriEra.ANNO_HEGIRAE, Locale.ENGLISH, TextWidth.SHORT, "AH" },
                // PersianEra
                { PersianEra.ANNO_PERSICO, Locale.ENGLISH, TextWidth.WIDE,  "AP" },
                { PersianEra.ANNO_PERSICO, Locale.GERMAN,  TextWidth.SHORT, "AP" },
                // CopticEra
                { CopticEra.ANNO_MARTYRUM, Locale.ENGLISH, TextWidth.WIDE,  "Year of the Martyrs" },
                { CopticEra.ANNO_MARTYRUM, Locale.GERMAN,  TextWidth.SHORT, "A.M." },
                // EthiopianEra wide
                { EthiopianEra.AMETE_ALEM,   Locale.ENGLISH, TextWidth.WIDE, "Year of the World" },
                { EthiopianEra.AMETE_MIHRET, Locale.ENGLISH, TextWidth.WIDE, "Year of Grace" },
                { EthiopianEra.AMETE_ALEM,   Locale.ROOT,    TextWidth.WIDE, "Amete Alem" },
                { EthiopianEra.AMETE_MIHRET, Locale.ROOT,    TextWidth.WIDE, "Amete Mihret" },
                // EthiopianEra short
                { EthiopianEra.AMETE_ALEM,   Locale.ENGLISH, TextWidth.SHORT, "Amete Alem" },
                { EthiopianEra.AMETE_MIHRET, Locale.ENGLISH, TextWidth.SHORT, "Amete Mihret" },
                { EthiopianEra.AMETE_ALEM,   Locale.ROOT,    TextWidth.SHORT, "Amete Alem" },
                { EthiopianEra.AMETE_MIHRET, Locale.ROOT,    TextWidth.SHORT, "Amete Mihret" },
                // MinguoEra
                { MinguoEra.ROC, Locale.GERMAN, TextWidth.WIDE,  "Minguo" },
                { MinguoEra.ROC, Locale.GERMAN, TextWidth.SHORT, "Minguo" },
                // ThaiSolarEra wide
                { ThaiSolarEra.RATTANAKOSIN, Locale.ENGLISH, TextWidth.WIDE, "Rattanakosin Sok" },
                { ThaiSolarEra.BUDDHIST,     Locale.ENGLISH, TextWidth.WIDE, "Buddhist Era" },
                { ThaiSolarEra.RATTANAKOSIN, Locale.FRANCE,  TextWidth.WIDE, "Rattanakosin Sok" },
                { ThaiSolarEra.BUDDHIST,     Locale.FRANCE,  TextWidth.WIDE, "ère bouddhique" },
                // ThaiSolarEra short
                { ThaiSolarEra.RATTANAKOSIN, Locale.ENGLISH, TextWidth.SHORT, "R.S." },
                { ThaiSolarEra.BUDDHIST,     Locale.ENGLISH, TextWidth.SHORT, "BE" },
                // IndianEra (no TextWidth overload)
                { IndianEra.SAKA, Locale.ENGLISH, null, "Saka" },
                { IndianEra.SAKA, new Locale("hi","IN"), null, "शक" },
                // ChineseEra (no TextWidth overload)
                { ChineseEra.QING_SHUNZHI_1644_1662, Locale.ROOT,    null, "Shunzhi" },
                { ChineseEra.QING_SHUNZHI_1644_1662, Locale.ENGLISH, null, "Shùnzhì" },
                { ChineseEra.QING_SHUNZHI_1644_1662, Locale.CHINA,   null, "順治" }
        });
    }

    private final Enum<?> era;
    private final Locale locale;
    private final TextWidth width;
    private final String expected;

    public EraNameTest(Enum<?> era, Locale locale, TextWidth width, String expected) {
        this.era = era;
        this.locale = locale;
        this.width = width;
        this.expected = expected;
    }

    @Test
    public void testDisplayName() throws Exception {
        String actual;
        if (width != null) {
            actual = (String) era.getClass()
                    .getMethod("getDisplayName", Locale.class, TextWidth.class)
                    .invoke(era, locale, width);
        } else {
            actual = (String) era.getClass()
                    .getMethod("getDisplayName", Locale.class)
                    .invoke(era, locale);
        }
        assertThat(actual, is(expected));
    }
}