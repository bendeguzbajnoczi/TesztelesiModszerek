package net.time4j.format;

import org.junit.Test;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Locale;
import static org.junit.Assert.*;

/**
 * A CalendarText osztályon belüli JDKTextProvider belső osztály viselkedésének vizsgálata
 * Ebben az esetben Reflectiont használtunk, hogy tudjuk példányosítani, mivel egy privát belső osztály
 * Ez az osztály a Locale alapú dátumszövegek (hónapok, napok, érák, stb.) lokalizált kezeléséért felelős
 */
public class CalendarTextJDKTextProviderReflectionTest {
    /**
     * Visszaad egy példányt a JDKTextProvider belső privát osztályból Reflection segítségével
     */
    private Object getProviderInstance() throws Exception {
        Class<?> clazz = Class.forName("net.time4j.format.CalendarText$JDKTextProvider");
        Constructor<?> ctor = clazz.getDeclaredConstructor();
        ctor.setAccessible(true);
        return ctor.newInstance();
    }

    /**
     * Meghívja a létrehozott példány adott metódusát
     *
     * @param methodName    a meghívandó metódus neve
     * @param paramTypes    a metódus paramétertípusai
     * @param args          a metódusnak átadott argumentumok
     * @return              a metódus visszatérési értéke
     * @throws Exception    bármilyen kivételt dobhat
     */
    private Object invokeMethod(String methodName, Class<?>[] paramTypes, Object... args) throws Exception {
        Object provider = getProviderInstance();
        Method method = provider.getClass().getMethod(methodName, paramTypes);
        return method.invoke(provider, args);
    }

    /**
     * Ellenőrzi, hogy a támogatott naptártípusok listája pontosan az "iso8601" típust tartalmazza
     */
    @Test
    public void testSupportedCalendarTypes() throws Exception {
        String[] result = (String[]) invokeMethod("getSupportedCalendarTypes", new Class<?>[]{});
        assertArrayEquals(new String[]{"iso8601"}, result);
    }

    /**
     * Ellenőrzi, hogy az elérhető lokalizációk között megtalálható-e az angol nyelv
     */
    @Test
    public void testAvailableLocalesContainsEnglish() throws Exception {
        Locale[] locales = (Locale[]) invokeMethod("getAvailableLocales", new Class<?>[]{});
        boolean found = false;
        for (Locale loc : locales) {
            if (loc.equals(Locale.ENGLISH)) {
                found = true;
                break;
            }
        }
        assertTrue("Locale.ENGLISH should be available", found);
    }

    /**
     * Teszteli az angol nyelvű, rövidített formátumú negyedévek visszaadását
     */
    @Test
    public void testQuartersEnglishAbbreviated() throws Exception {
        String[] quarters = (String[]) invokeMethod(
                "quarters",
                new Class<?>[]{String.class, Locale.class, TextWidth.class, OutputContext.class},
                "iso8601", Locale.ENGLISH, TextWidth.ABBREVIATED, OutputContext.FORMAT
        );
        assertEquals(4, quarters.length);
        assertTrue(quarters[0].toLowerCase(Locale.ENGLISH).contains("1"));
    }

    /**
     * Teszteli a német nyelvű, rövidített hét napjainak visszaadását
     */
    @Test
    public void testWeekdaysGermanShortStandalone() throws Exception {
        String[] weekdays = (String[]) invokeMethod(
                "weekdays",
                new Class<?>[]{String.class, Locale.class, TextWidth.class, OutputContext.class},
                "iso8601", Locale.GERMAN, TextWidth.SHORT, OutputContext.STANDALONE
        );
        assertEquals(7, weekdays.length);
        boolean foundMo = false;
        for (String day : weekdays) {
            if (day.toLowerCase(Locale.GERMAN).contains("mo")) {
                foundMo = true;
                break;
            }
        }
        assertTrue(foundMo);
    }

    /**
     * Ellenőrzi, hogy a japán nyelvű érák helyesen kerülnek-e visszaadásra széles szöveg formátumban
     */
    @Test
    public void testErasJapaneseWide() throws Exception {
        String[] eras = (String[]) invokeMethod(
                "eras",
                new Class<?>[]{String.class, Locale.class, TextWidth.class},
                "iso8601", Locale.JAPANESE, TextWidth.WIDE
        );
        assertEquals(2, eras.length); // BC, AD
        assertTrue(eras[0] != null && !eras[0].isEmpty());
    }

    /**
     * Teszteli a francia nyelvű, szűk formátumban megjelenített napszakokat (délelőtt/délután)
     */
    @Test
    public void testMeridiemsFrenchNarrowFormat() throws Exception {
        String[] meridiems = (String[]) invokeMethod(
                "meridiems",
                new Class<?>[]{String.class, Locale.class, TextWidth.class, OutputContext.class},
                "iso8601", Locale.FRENCH, TextWidth.NARROW, OutputContext.FORMAT
        );
        assertEquals(2, meridiems.length);
        assertTrue(meridiems[0].toLowerCase().contains("a") || meridiems[0].toLowerCase().contains("m"));
    }

    /**
     * Ellenőrzi a toString() metódus visszatérési értékét.
     */
    @Test
    public void testToString() throws Exception {
        Object provider = getProviderInstance();
        String result = provider.toString();
        assertEquals("JDKTextProvider", result);
    }

    /**
     * Indirekt módon teszteli a getStyle() helper metódust
     * Egy szűk formátumú hónapnevet kér le angol nyelven, és ellenőrzi az eredményt
     */
    @Test
    public void testGetStyleHelperIndirect() throws Exception {
        String[] months = (String[]) invokeMethod(
                "months",
                new Class<?>[]{String.class, Locale.class, TextWidth.class, OutputContext.class, boolean.class},
                "iso8601", Locale.ENGLISH, TextWidth.NARROW, OutputContext.STANDALONE, false
        );
        assertEquals(12, months.length);
        assertNotNull(months[0]);
    }
}
