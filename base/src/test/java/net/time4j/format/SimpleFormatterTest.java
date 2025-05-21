package net.time4j.format;

import net.time4j.Moment;
import net.time4j.PlainDate;
import net.time4j.PlainTime;
import net.time4j.PlainTimestamp;
import net.time4j.format.platform.SimpleFormatter;
import net.time4j.tz.TZID;
import net.time4j.tz.Timezone;
import org.junit.Test;

import java.lang.reflect.Method;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.format.FormatStyle;
import java.util.Locale;
import static org.junit.Assert.*;

/**
 * A SimpleFormatter osztály viselkedésének vizsgálata
 * Ez az osztály időpontok és dátumok formázására és értelmezésére (parse)
 * szolgál, különböző lokalizációs beállítások és formátumstílusok esetén
 */
public class SimpleFormatterTest {
    /**
     * Teszteli, hogy az ofTimestampStyle() metódus helyesen hoz létre egy
     * SimpleFormatter<PlainTimestamp> példányt
     */
    @Test
    public void testOfTimestampStyle() {
        SimpleFormatter<PlainTimestamp> formatter = SimpleFormatter.ofTimestampStyle(
                FormatStyle.SHORT, FormatStyle.SHORT, Locale.US);
        assertNotNull(formatter);
    }

    /**
     * Teszteli, hogy az ofMomentStyle() metódus helyesen hoz létre egy
     * SimpleFormatter<Moment> példányt egy adott időzónát használva
     */
    @Test
    public void testOfMomentStyle() {
        TZID tzid = Timezone.of("Europe/Budapest").getID();
        SimpleFormatter<Moment> formatter = SimpleFormatter.ofMomentStyle(
                FormatStyle.SHORT, FormatStyle.SHORT, Locale.US, tzid);
        assertNotNull(formatter);
    }

    /**
     * Ellenőrzi, hogy két azonos stílusú és lokalizációjú formatter egyenlő,
     * míg eltérő formázással nem egyezik meg
     */
    @Test
    public void testEquals() {
        SimpleFormatter<PlainTimestamp> formatter1 = SimpleFormatter.ofTimestampStyle(
                FormatStyle.SHORT, FormatStyle.SHORT, Locale.US);
        SimpleFormatter<PlainTimestamp> formatter2 = SimpleFormatter.ofTimestampStyle(
                FormatStyle.SHORT, FormatStyle.SHORT, Locale.US);
        SimpleFormatter<PlainTimestamp> formatter3 = SimpleFormatter.ofTimestampStyle(
                FormatStyle.MEDIUM, FormatStyle.SHORT, Locale.US);

        assertEquals(formatter1, formatter2);
        assertNotEquals(formatter1, formatter3);
    }

    /**
     * Teszteli a toOldApiValue() metódust Reflection segítségével
     * Ez a metódus a FormatStyle-ból visszaadja a Java API-nak megfelelő formázási konstansot
     * @throws Exception    bármilyen kivételt dobhat
     */
    @Test
    public void testToOldApiValue() throws Exception {
        Method method = SimpleFormatter.class.getDeclaredMethod("toOldApiValue", FormatStyle.class);
        method.setAccessible(true);
        int dfStyle = (int) method.invoke(null, FormatStyle.SHORT);
        DateFormat dateFormat = DateFormat.getDateInstance(dfStyle, Locale.US);
        assertNotNull(dateFormat);
        assertTrue(dateFormat instanceof SimpleDateFormat);
    }

    /**
     * Teszteli a getFormatPattern() metódust Reflection segítségével
     * Ez a metódus a DateFormat-ból visszaadja a formázási mintát stringként
     * @throws Exception    bármilyen kivételt dobhat
     */
    @Test
    public void testGetFormatPattern() throws Exception {
        SimpleFormatter<PlainTimestamp> formatter = SimpleFormatter.ofTimestampStyle(
                FormatStyle.SHORT, FormatStyle.SHORT, Locale.US);
        Method method = SimpleFormatter.class.getDeclaredMethod("toOldApiValue", FormatStyle.class);
        method.setAccessible(true);
        int dfStyle = (int) method.invoke(null, FormatStyle.SHORT);
        DateFormat dateFormat = DateFormat.getDateInstance(dfStyle, Locale.US);
        Method getPatternMethod = SimpleFormatter.class.getDeclaredMethod("getFormatPattern", DateFormat.class);
        getPatternMethod.setAccessible(true);
        String pattern = (String) getPatternMethod.invoke(formatter, dateFormat);
        assertNotNull(pattern);
        assertFalse(pattern.isEmpty());
    }

    /**
     * Ellenőrzi, hogy két azonos paraméterekkel létrehozott SimpleFormatter
     * példány ugyanazt a hash kódot adja vissza
     */
    @Test
    public void testHashCodeSameFormatters() {
        SimpleFormatter<Moment> f1 = SimpleFormatter.ofMomentStyle(
                FormatStyle.SHORT, FormatStyle.SHORT, Locale.US, Timezone.of("Europe/Budapest").getID()
        );
        SimpleFormatter<Moment> f2 = SimpleFormatter.ofMomentStyle(
                FormatStyle.SHORT, FormatStyle.SHORT, Locale.US, Timezone.of("Europe/Budapest").getID()
        );
        assertEquals(f1.hashCode(), f2.hashCode());
    }

    /**
     * Ellenőrzi, hogy két eltérő formázási stílusokkal létrehozott SimpleFormatter
     * példány különböző hash kódokat ad vissza
     */
    @Test
    public void testHashCodeDifferentFormatters() {
        SimpleFormatter<Moment> f1 = SimpleFormatter.ofMomentStyle(
                FormatStyle.SHORT, FormatStyle.SHORT, Locale.US, Timezone.of("Europe/Budapest").getID()
        );
        SimpleFormatter<Moment> f2 = SimpleFormatter.ofMomentStyle(
                FormatStyle.MEDIUM, FormatStyle.SHORT, Locale.US, Timezone.of("Europe/Budapest").getID()
        );
        assertNotEquals(f1.hashCode(), f2.hashCode());
    }

    /**
     * Teszteli, hogy létrejön-e egy SimpleFormatter példány PlainTime típusra,
     * rövid időformázási stílussal és amerikai lokalizációval
     */
    @Test
    public void testOfTimeStyleFormatStyleShort() {
        SimpleFormatter<PlainTime> formatter = SimpleFormatter.ofTimeStyle(FormatStyle.SHORT, Locale.US);
        assertNotNull(formatter);
    }

    /**
     * Teszteli, hogy létrejön-e egy SimpleFormatter példány PlainTime típusra,
     * közepes időformázási stílussal és francia lokalizációval
     */
    @Test
    public void testOfTimeStyleFormatStyleMedium() {
        SimpleFormatter<PlainTime> formatter = SimpleFormatter.ofTimeStyle(FormatStyle.MEDIUM, Locale.FRANCE);
        assertNotNull(formatter);
    }

    /**
     * Teszteli, hogy létrejön-e egy SimpleFormatter PlainDate típusra,
     * rövid dátumformátummal és amerikai lokalizációval
     */
    @Test
    public void testOfDateStyleUS() {
        SimpleFormatter<PlainDate> formatter = SimpleFormatter.ofDateStyle(FormatStyle.SHORT, Locale.US);
        assertNotNull(formatter);
    }

    /**
     * Teszteli, hogy létrejön-e egy SimpleFormatter PlainDate típusra,
     * teljes dátumformátummal és német lokalizációval
     */
    @Test
    public void testOfDateStyleGermany() {
        SimpleFormatter<PlainDate> formatter = SimpleFormatter.ofDateStyle(FormatStyle.FULL, Locale.GERMANY);
        assertNotNull(formatter);
    }

    /**
     * Teszteli, hogy létrejön-e egy SimpleFormatter Moment típusra rövid
     * dátum- és időformátummal, amerikai lokalizációval és budapesti időzónával
     */
    @Test
    public void testOfMomentStyleShortShort() {
        SimpleFormatter<Moment> formatter = SimpleFormatter.ofMomentStyle(
                FormatStyle.SHORT, FormatStyle.SHORT, Locale.US, Timezone.of("Europe/Budapest").getID()
        );
        assertNotNull(formatter);
    }

    /**
     * Teszteli, hogy létrejön-e egy SimpleFormatter Moment típusra
     * teljes dátum- és közepes időformátummal, francia kanadai lokalizációval
     * és párizsi időzónával
     */
    @Test
    public void testOfMomentStyleFullMedium() {
        SimpleFormatter<Moment> formatter = SimpleFormatter.ofMomentStyle(
                FormatStyle.FULL, FormatStyle.MEDIUM, Locale.CANADA_FRENCH, Timezone.of("Europe/Paris").getID());
        assertNotNull(formatter);
    }

    /**
     * Teszteli, hogy létrejön-e egy SimpleFormatter PlainTimestamp típusra
     * rövid dátum- és közepes időformátummal, olasz lokalizációval
     */
    @Test
    public void testOfTimestampStyleShortMedium() {
        SimpleFormatter<PlainTimestamp> formatter = SimpleFormatter.ofTimestampStyle(
                FormatStyle.SHORT, FormatStyle.MEDIUM, Locale.ITALY);
        assertNotNull(formatter);
    }

    /**
     * Teszteli, hogy létrejön-e egy SimpleFormatter PlainTimestamp típusra
     * teljes dátum- és rövid időformátummal, japán lokalizációval
     */
    @Test
    public void testOfTimestampStyleFullShort() {
        SimpleFormatter<PlainTimestamp> formatter = SimpleFormatter.ofTimestampStyle(
                FormatStyle.FULL, FormatStyle.SHORT, Locale.JAPAN);
        assertNotNull(formatter);
    }

    /**
     * Ellenőrzi, hogy a with(Locale) metódus eltérő lokalizációval eltérő
     * formatter példányt ad vissza
     */
    @Test
    public void testWithDifferentLocale() {
        SimpleFormatter<PlainDate> formatter = SimpleFormatter.ofDateStyle(FormatStyle.SHORT, Locale.US);
        TemporalFormatter<PlainDate> changed = formatter.with(Locale.GERMANY);
        assertNotEquals(formatter, changed);
    }

    /**
     * Ellenőrzi, hogy a with(Locale) metódus azonos lokalizációval azonos
     * formatter példányt ad vissza
     */
    @Test
    public void testWithSameLocale() {
        SimpleFormatter<PlainDate> formatter = SimpleFormatter.ofDateStyle(FormatStyle.SHORT, Locale.US);
        TemporalFormatter<PlainDate> changed = formatter.with(Locale.US);
        assertEquals(formatter, changed);
    }

    /**
     * Teszteli, hogy szigorú engedékenység (leniency) esetén nem null-t
     * ad vissza
     */
    @Test
    public void testWithLeniencyStrict() {
        SimpleFormatter<PlainDate> formatter = SimpleFormatter.ofDateStyle(FormatStyle.SHORT, Locale.US);
        TemporalFormatter<PlainDate> strict = formatter.with(Leniency.STRICT);
        assertNotNull(strict);
    }

    /**
     * Teszteli, hogy LAX (enyhébb) engedékenység (leniency) esetén nem null-t
     * ad vissza
     */
    @Test
    public void testWithLeniencyLax() {
        SimpleFormatter<PlainDate> formatter = SimpleFormatter.ofDateStyle(FormatStyle.SHORT, Locale.US);
        TemporalFormatter<PlainDate> lax = formatter.with(Leniency.LAX);
        assertNotNull(lax);
    }

    /**
     * Teszteli, hogy érvényes időformátummintával létrejön-e SimpleFormatter
     * PlainTime típusra
     */
    @Test
    public void testOfTimePatternValid() {
        SimpleFormatter<PlainTime> formatter = SimpleFormatter.ofTimePattern("HH:mm:ss", Locale.US);
        assertNotNull(formatter);
    }

    /**
     * Teszteli, hogy napszak (AM/PM) jelölést tartalmazó időformátummintával is
     * létrejön-e SimpleFormatter
     */
    @Test
    public void testOfTimePatternWithAmPm() {
        SimpleFormatter<PlainTime> formatter = SimpleFormatter.ofTimePattern("hh:mm a", Locale.US);
        assertNotNull(formatter);
    }
}
