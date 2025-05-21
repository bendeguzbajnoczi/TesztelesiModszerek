package net.time4j.calendar;

import net.time4j.PlainDate;
import net.time4j.calendar.astro.AstronomicalSeason;
import net.time4j.engine.CalendarDays;
import net.time4j.format.expert.ChronoFormatter;
import net.time4j.format.expert.PatternType;
import net.time4j.tz.OffsetSign;
import net.time4j.tz.ZonalOffset;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.text.ParseException;
import java.util.Locale;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(JUnit4.class)
public class SolarTermRefactoredTest {

    @Test
    public void getDisplayNameChinese() {
        assertThat(
            SolarTerm.MAJOR_11_DONGZHI_270.getDisplayName(Locale.CHINA),
            is("冬至"));
    }

    @Test
    public void parseChinese() throws ParseException {
        assertThat(
            SolarTerm.parse("冬至", Locale.CHINA),
            is(SolarTerm.MAJOR_11_DONGZHI_270));
    }

    @Test
    public void parseEnglish() throws ParseException {
        assertThat(
            SolarTerm.parse("dōngzhì", Locale.ENGLISH),
            is(SolarTerm.MAJOR_11_DONGZHI_270));
        assertThat(
            SolarTerm.parse("Dongzhi", Locale.ENGLISH), // case-insensitive search
            is(SolarTerm.MAJOR_11_DONGZHI_270));
    }

    @Test
    public void roll() {
        assertThat(
            SolarTerm.MAJOR_11_DONGZHI_270.roll(7),
            is(SolarTerm.MINOR_03_QINGMING_015));
    }

    @Test
    public void ofMajor() {
        Object[][] cases = {
                { 1, SolarTerm.MAJOR_01_YUSHUI_330 },
                { 2, SolarTerm.MAJOR_02_CHUNFEN_000 },
                { 3, SolarTerm.MAJOR_03_GUYU_030 },
                { 4, SolarTerm.MAJOR_04_XIAOMAN_060 },
                { 5, SolarTerm.MAJOR_05_XIAZHI_090 },
                { 6, SolarTerm.MAJOR_06_DASHU_120 },
                { 7, SolarTerm.MAJOR_07_CHUSHU_150 },
                { 8, SolarTerm.MAJOR_08_QIUFEN_180 },
                { 9, SolarTerm.MAJOR_09_SHUANGJIANG_210 },
                { 10, SolarTerm.MAJOR_10_XIAOXUE_240 },
                { 11, SolarTerm.MAJOR_11_DONGZHI_270 },
                { 12, SolarTerm.MAJOR_12_DAHAN_300 }
        };

        for (Object[] c : cases) {
            int input = (int) c[0];
            SolarTerm expected = (SolarTerm) c[1];
            assertThat("ofMajor(" + input + ")", SolarTerm.ofMajor(input), is(expected));
        }
    }

    @Test
    public void isMajor() {
        Object[][] cases = {
                { SolarTerm.MINOR_01_LICHUN_315, false },
                { SolarTerm.MINOR_02_JINGZHE_345, false },
                { SolarTerm.MINOR_03_QINGMING_015, false },
                { SolarTerm.MINOR_04_LIXIA_045, false },
                { SolarTerm.MINOR_05_MANGZHONG_075, false },
                { SolarTerm.MINOR_06_XIAOSHU_105, false },
                { SolarTerm.MINOR_07_LIQIU_135, false },
                { SolarTerm.MINOR_08_BAILU_165, false },
                { SolarTerm.MINOR_09_HANLU_195, false },
                { SolarTerm.MINOR_10_LIDONG_225, false },
                { SolarTerm.MINOR_11_DAXUE_255, false },
                { SolarTerm.MINOR_12_XIAOHAN_285, false },
                { SolarTerm.MAJOR_01_YUSHUI_330, true },
                { SolarTerm.MAJOR_02_CHUNFEN_000, true },
                { SolarTerm.MAJOR_03_GUYU_030, true },
                { SolarTerm.MAJOR_04_XIAOMAN_060, true },
                { SolarTerm.MAJOR_05_XIAZHI_090, true },
                { SolarTerm.MAJOR_06_DASHU_120, true },
                { SolarTerm.MAJOR_07_CHUSHU_150, true },
                { SolarTerm.MAJOR_08_QIUFEN_180, true },
                { SolarTerm.MAJOR_09_SHUANGJIANG_210, true },
                { SolarTerm.MAJOR_10_XIAOXUE_240, true },
                { SolarTerm.MAJOR_11_DONGZHI_270, true },
                { SolarTerm.MAJOR_12_DAHAN_300, true }
        };

        for (Object[] c : cases) {
            SolarTerm term = (SolarTerm) c[0];
            boolean expected = (boolean) c[1];
            assertThat("isMajor of " + term.name(), term.isMajor(), is(expected));
        }
    }


    @Test
    public void ofMinor() {
        Object[][] cases = {
                { 1, SolarTerm.MINOR_01_LICHUN_315 },
                { 2, SolarTerm.MINOR_02_JINGZHE_345 },
                { 3, SolarTerm.MINOR_03_QINGMING_015 },
                { 4, SolarTerm.MINOR_04_LIXIA_045 },
                { 5, SolarTerm.MINOR_05_MANGZHONG_075 },
                { 6, SolarTerm.MINOR_06_XIAOSHU_105 },
                { 7, SolarTerm.MINOR_07_LIQIU_135 },
                { 8, SolarTerm.MINOR_08_BAILU_165 },
                { 9, SolarTerm.MINOR_09_HANLU_195 },
                { 10, SolarTerm.MINOR_10_LIDONG_225 },
                { 11, SolarTerm.MINOR_11_DAXUE_255 },
                { 12, SolarTerm.MINOR_12_XIAOHAN_285 }
        };

        for (Object[] c : cases) {
            int input = (int) c[0];
            SolarTerm expected = (SolarTerm) c[1];
            assertThat("ofMinor(" + input + ")", SolarTerm.ofMinor(input), is(expected));
        }
    }

    @Test
    public void isMinor() {
        Object[][] cases = {
                { SolarTerm.MINOR_01_LICHUN_315, true },
                { SolarTerm.MINOR_02_JINGZHE_345, true },
                { SolarTerm.MINOR_03_QINGMING_015, true },
                { SolarTerm.MINOR_04_LIXIA_045, true },
                { SolarTerm.MINOR_05_MANGZHONG_075, true },
                { SolarTerm.MINOR_06_XIAOSHU_105, true },
                { SolarTerm.MINOR_07_LIQIU_135, true },
                { SolarTerm.MINOR_08_BAILU_165, true },
                { SolarTerm.MINOR_09_HANLU_195, true },
                { SolarTerm.MINOR_10_LIDONG_225, true },
                { SolarTerm.MINOR_11_DAXUE_255, true },
                { SolarTerm.MINOR_12_XIAOHAN_285, true },
                { SolarTerm.MAJOR_01_YUSHUI_330, false },
                { SolarTerm.MAJOR_02_CHUNFEN_000, false },
                { SolarTerm.MAJOR_03_GUYU_030, false },
                { SolarTerm.MAJOR_04_XIAOMAN_060, false },
                { SolarTerm.MAJOR_05_XIAZHI_090, false },
                { SolarTerm.MAJOR_06_DASHU_120, false },
                { SolarTerm.MAJOR_07_CHUSHU_150, false },
                { SolarTerm.MAJOR_08_QIUFEN_180, false },
                { SolarTerm.MAJOR_09_SHUANGJIANG_210, false },
                { SolarTerm.MAJOR_10_XIAOXUE_240, false },
                { SolarTerm.MAJOR_11_DONGZHI_270, false },
                { SolarTerm.MAJOR_12_DAHAN_300, false }
        };

        for (Object[] c : cases) {
            SolarTerm term = (SolarTerm) c[0];
            boolean expected = (boolean) c[1];
            assertThat("isMinor of " + term.name(), term.isMinor(), is(expected));
        }
    }

    @Test
    public void getIndex() {
        Object[][] cases = {
                { SolarTerm.MINOR_01_LICHUN_315, 1 },
                { SolarTerm.MINOR_02_JINGZHE_345, 2 },
                { SolarTerm.MINOR_03_QINGMING_015, 3 },
                { SolarTerm.MINOR_04_LIXIA_045, 4 },
                { SolarTerm.MINOR_05_MANGZHONG_075, 5 },
                { SolarTerm.MINOR_06_XIAOSHU_105, 6 },
                { SolarTerm.MINOR_07_LIQIU_135, 7 },
                { SolarTerm.MINOR_08_BAILU_165, 8 },
                { SolarTerm.MINOR_09_HANLU_195, 9 },
                { SolarTerm.MINOR_10_LIDONG_225, 10 },
                { SolarTerm.MINOR_11_DAXUE_255, 11 },
                { SolarTerm.MINOR_12_XIAOHAN_285, 12 },
                { SolarTerm.MAJOR_01_YUSHUI_330, 1 },
                { SolarTerm.MAJOR_02_CHUNFEN_000, 2 },
                { SolarTerm.MAJOR_03_GUYU_030, 3 },
                { SolarTerm.MAJOR_04_XIAOMAN_060, 4 },
                { SolarTerm.MAJOR_05_XIAZHI_090, 5 },
                { SolarTerm.MAJOR_06_DASHU_120, 6 },
                { SolarTerm.MAJOR_07_CHUSHU_150, 7 },
                { SolarTerm.MAJOR_08_QIUFEN_180, 8 },
                { SolarTerm.MAJOR_09_SHUANGJIANG_210, 9 },
                { SolarTerm.MAJOR_10_XIAOXUE_240, 10 },
                { SolarTerm.MAJOR_11_DONGZHI_270, 11 },
                { SolarTerm.MAJOR_12_DAHAN_300, 12 }
        };

        for (Object[] c : cases) {
            SolarTerm term = (SolarTerm) c[0];
            int expected = (int) c[1];
            assertThat("getIndex of " + term.name(), term.getIndex(), is(expected));
        }
    }


    @Test
    public void getSolarLongitude() {
        Object[][] cases = {
                { SolarTerm.MINOR_01_LICHUN_315, 315 },
                { SolarTerm.MINOR_02_JINGZHE_345, 345 },
                { SolarTerm.MINOR_03_QINGMING_015, 15 },
                { SolarTerm.MINOR_04_LIXIA_045, 45 },
                { SolarTerm.MINOR_05_MANGZHONG_075, 75 },
                { SolarTerm.MINOR_06_XIAOSHU_105, 105 },
                { SolarTerm.MINOR_07_LIQIU_135, 135 },
                { SolarTerm.MINOR_08_BAILU_165, 165 },
                { SolarTerm.MINOR_09_HANLU_195, 195 },
                { SolarTerm.MINOR_10_LIDONG_225, 225 },
                { SolarTerm.MINOR_11_DAXUE_255, 255 },
                { SolarTerm.MINOR_12_XIAOHAN_285, 285 },
                { SolarTerm.MAJOR_01_YUSHUI_330, 330 },
                { SolarTerm.MAJOR_02_CHUNFEN_000, 0 },
                { SolarTerm.MAJOR_03_GUYU_030, 30 },
                { SolarTerm.MAJOR_04_XIAOMAN_060, 60 },
                { SolarTerm.MAJOR_05_XIAZHI_090, 90 },
                { SolarTerm.MAJOR_06_DASHU_120, 120 },
                { SolarTerm.MAJOR_07_CHUSHU_150, 150 },
                { SolarTerm.MAJOR_08_QIUFEN_180, 180 },
                { SolarTerm.MAJOR_09_SHUANGJIANG_210, 210 },
                { SolarTerm.MAJOR_10_XIAOXUE_240, 240 },
                { SolarTerm.MAJOR_11_DONGZHI_270, 270 },
                { SolarTerm.MAJOR_12_DAHAN_300, 300 }
        };

        for (Object[] c : cases) {
            SolarTerm term = (SolarTerm) c[0];
            int expected = (int) c[1];
            assertThat("getSolarLongitude of " + term.name(), term.getSolarLongitude(), is(expected));
        }
    }


    @Test
    public void getSolarTerm() {
        assertThat(
            PlainDate.of(1989, 12, 21).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_11_DAXUE_255));
        assertThat(
            PlainDate.of(1989, 12, 22).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_11_DONGZHI_270));
        assertThat(
            AstronomicalSeason.WINTER_SOLSTICE
                .inYear(1989)
                .toZonalTimestamp(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8))
                .getCalendarDate(),
            is(PlainDate.of(1989, 12, 22)));

        assertThat(
            PlainDate.of(2018, 1, 4).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_11_DONGZHI_270));
        assertThat(
            PlainDate.of(2018, 1, 5).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_12_XIAOHAN_285));
        assertThat(
            PlainDate.of(2018, 1, 19).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_12_XIAOHAN_285));
        assertThat(
            PlainDate.of(2018, 1, 20).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_12_DAHAN_300));

        assertThat(
            PlainDate.of(2018, 2, 3).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_12_DAHAN_300));
        assertThat(
            PlainDate.of(2018, 2, 4).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_01_LICHUN_315));
        assertThat(
            PlainDate.of(2018, 2, 18).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_01_LICHUN_315));
        assertThat(
            PlainDate.of(2018, 2, 19).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_01_YUSHUI_330));

        assertThat(
            PlainDate.of(2018, 3, 4).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_01_YUSHUI_330));
        assertThat(
            PlainDate.of(2018, 3, 5).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_02_JINGZHE_345));
        assertThat(
            PlainDate.of(2018, 3, 20).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_02_JINGZHE_345));
        assertThat(
            PlainDate.of(2018, 3, 21).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_02_CHUNFEN_000));
        assertThat(
            AstronomicalSeason.VERNAL_EQUINOX
                .inYear(2018)
                .toZonalTimestamp(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8))
                .getCalendarDate(),
            is(PlainDate.of(2018, 3, 21)));

        assertThat(
            PlainDate.of(2018, 4, 4).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_02_CHUNFEN_000));
        assertThat(
            PlainDate.of(2018, 4, 5).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_03_QINGMING_015));
        assertThat(
            PlainDate.of(2018, 4, 19).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_03_QINGMING_015));
        assertThat(
            PlainDate.of(2018, 4, 20).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_03_GUYU_030));

        assertThat(
            PlainDate.of(2018, 5, 4).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_03_GUYU_030));
        assertThat(
            PlainDate.of(2018, 5, 5).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_04_LIXIA_045));
        assertThat(
            PlainDate.of(2018, 5, 20).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_04_LIXIA_045));
        assertThat(
            PlainDate.of(2018, 5, 21).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_04_XIAOMAN_060));

        assertThat(
            PlainDate.of(2018, 6, 5).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_04_XIAOMAN_060));
        assertThat(
            PlainDate.of(2018, 6, 6).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_05_MANGZHONG_075));
        assertThat(
            PlainDate.of(2018, 6, 20).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_05_MANGZHONG_075));
        assertThat(
            PlainDate.of(2018, 6, 21).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_05_XIAZHI_090));
        assertThat(
            AstronomicalSeason.SUMMER_SOLSTICE
                .inYear(2018)
                .toZonalTimestamp(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8))
                .getCalendarDate(),
            is(PlainDate.of(2018, 6, 21)));

        assertThat(
            PlainDate.of(2018, 7, 6).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_05_XIAZHI_090));
        assertThat(
            PlainDate.of(2018, 7, 7).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_06_XIAOSHU_105));
        assertThat(
            PlainDate.of(2018, 7, 22).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_06_XIAOSHU_105));
        assertThat(
            PlainDate.of(2018, 7, 23).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_06_DASHU_120));

        assertThat(
            PlainDate.of(2018, 8, 6).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_06_DASHU_120));
        assertThat(
            PlainDate.of(2018, 8, 7).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_07_LIQIU_135));
        assertThat(
            PlainDate.of(2018, 8, 22).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_07_LIQIU_135));
        assertThat(
            PlainDate.of(2018, 8, 23).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_07_CHUSHU_150));

        assertThat(
            PlainDate.of(2018, 9, 7).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_07_CHUSHU_150));
        assertThat(
            PlainDate.of(2018, 9, 8).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_08_BAILU_165));
        assertThat(
            PlainDate.of(2018, 9, 22).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_08_BAILU_165));
        assertThat(
            PlainDate.of(2018, 9, 23).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_08_QIUFEN_180));
        assertThat(
            AstronomicalSeason.AUTUMNAL_EQUINOX
                .inYear(2018)
                .toZonalTimestamp(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8))
                .getCalendarDate(),
            is(PlainDate.of(2018, 9, 23)));

        assertThat(
            PlainDate.of(2018, 10, 7).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_08_QIUFEN_180));
        assertThat(
            PlainDate.of(2018, 10, 8).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_09_HANLU_195));
        assertThat(
            PlainDate.of(2018, 10, 22).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_09_HANLU_195));
        assertThat(
            PlainDate.of(2018, 10, 23).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_09_SHUANGJIANG_210));

        assertThat(
            PlainDate.of(2018, 11, 6).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_09_SHUANGJIANG_210));
        assertThat(
            PlainDate.of(2018, 11, 7).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_10_LIDONG_225));
        assertThat(
            PlainDate.of(2018, 11, 21).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_10_LIDONG_225));
        assertThat(
            PlainDate.of(2018, 11, 22).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_10_XIAOXUE_240));

        assertThat(
            PlainDate.of(2018, 12, 6).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_10_XIAOXUE_240));
        assertThat(
            PlainDate.of(2018, 12, 7).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_11_DAXUE_255));
        assertThat(
            PlainDate.of(2018, 12, 21).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MINOR_11_DAXUE_255));
        assertThat(
            PlainDate.of(2018, 12, 22).transform(ChineseCalendar.axis()).getSolarTerm(),
            is(SolarTerm.MAJOR_11_DONGZHI_270));
        assertThat(
            AstronomicalSeason.WINTER_SOLSTICE
                .inYear(2018)
                .toZonalTimestamp(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8))
                .getCalendarDate(),
            is(PlainDate.of(2018, 12, 22)));
    }

    @Test
    public void format() throws ParseException {
        ChronoFormatter<ChineseCalendar> formatter =
            ChronoFormatter.setUp(ChineseCalendar.axis(), Locale.ENGLISH)
                .addPattern("EEE, d. MMMM r(U) ", PatternType.CLDR_DATE)
                .addText(ChineseCalendar.SOLAR_TERM)
                .build();
        PlainDate winter =
            AstronomicalSeason.WINTER_SOLSTICE
                .inYear(2018)
                .toZonalTimestamp(ZonalOffset.ofHours(OffsetSign.AHEAD_OF_UTC, 8))
                .getCalendarDate();
        ChineseCalendar chineseDate = winter.transform(ChineseCalendar.class);
        assertThat(
            formatter.with(Locale.CHINESE).parse("周六, 16. 十一月 2018(戊戌) 冬至"),
            is(chineseDate));
        assertThat(
            formatter.with(Locale.CHINESE).format(chineseDate),
            is("周六, 16. 十一月 2018(戊戌) 冬至"));
        assertThat(
            formatter.format(chineseDate),
            is("Sat, 16. Eleventh Month 2018(wù-xū) dōngzhì"));
    }

    @Test
    public void onOrAfter() {
        ChineseCalendar date = PlainDate.of(2017, 12, 22).transform(ChineseCalendar.axis());
        assertThat(
            SolarTerm.MAJOR_11_DONGZHI_270.onOrAfter(date),
            is(date));
        assertThat(
            SolarTerm.MAJOR_11_DONGZHI_270.onOrAfter(date.plus(CalendarDays.ONE)),
            is(PlainDate.of(2018, 12, 22).transform(ChineseCalendar.axis())));
    }

    @Test
    public void isValidNull() {
        ChineseCalendar date = PlainDate.of(2017, 12, 22).transform(ChineseCalendar.axis());
        assertThat(date.isValid(ChineseCalendar.SOLAR_TERM, null), is(false));
    }

    @Test(expected=IllegalArgumentException.class)
    public void withNull() {
        ChineseCalendar date = PlainDate.of(2017, 12, 22).transform(ChineseCalendar.axis());
        date.with(ChineseCalendar.SOLAR_TERM, null);
    }

    @Test
    public void withSolarTerm() {
        ChineseCalendar date = PlainDate.of(2017, 12, 22).transform(ChineseCalendar.axis());
        assertThat(
            date.with(ChineseCalendar.SOLAR_TERM, SolarTerm.MAJOR_11_DONGZHI_270),
            is(date));
        assertThat(
            date.minus(CalendarDays.of(100)).with(ChineseCalendar.SOLAR_TERM, SolarTerm.MAJOR_11_DONGZHI_270),
            is(date));
        assertThat( // new year
            date.with(ChineseCalendar.DAY_OF_YEAR, 1).with(ChineseCalendar.SOLAR_TERM, SolarTerm.MAJOR_11_DONGZHI_270),
            is(date));
        assertThat(
            date.with(ChineseCalendar.SOLAR_TERM, SolarTerm.MINOR_03_QINGMING_015),
            is(date.with(ChineseCalendar.MONTH_AS_ORDINAL, 3).with(ChineseCalendar.DAY_OF_MONTH, 8)));
    }

}