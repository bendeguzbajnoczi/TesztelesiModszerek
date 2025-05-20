package net.time4j.format;

import org.junit.Test;
import java.util.Locale;
import static org.junit.Assert.*;
import net.time4j.format.DefaultPluralProviderSPI;
import net.time4j.format.PluralCategory;

/**
 * A DefaultPluralProviderSPI osztályon belüli StdOrdinalRules belső osztály viselkedésének vizsgálata
 * Ez az osztály a sorszámnevek kezelését végzi el
 */
public class StdOrdinalRulesTest {
    /**
     * Alap típusellenőrzés a getter metódus segítségével
     */
    @Test
    public void testNumberType() {
        PluralRules rules = new DefaultPluralProviderSPI().load(Locale.ENGLISH, NumberType.ORDINALS);
        assertEquals(NumberType.ORDINALS, rules.getNumberType());
    }

    /**
     * Ellenőrzi az angol sorszámszabályokat (pl. first, second, third, fourth, ...)
     */
    @Test
    public void testEnglishOrdinalCategories() {
        PluralRules rules = new DefaultPluralProviderSPI().load(Locale.ENGLISH, NumberType.ORDINALS);

        assertEquals(PluralCategory.ONE, rules.getCategory(1));
        assertEquals(PluralCategory.TWO, rules.getCategory(2));
        assertEquals(PluralCategory.FEW, rules.getCategory(3));
        assertEquals(PluralCategory.OTHER, rules.getCategory(4));
        assertEquals(PluralCategory.OTHER, rules.getCategory(11));
        assertEquals(PluralCategory.OTHER, rules.getCategory(13));
        assertEquals(PluralCategory.ONE, rules.getCategory(21));
        assertEquals(PluralCategory.TWO, rules.getCategory(22));
        assertEquals(PluralCategory.FEW, rules.getCategory(23));
        assertEquals(PluralCategory.OTHER, rules.getCategory(24));
    }

    /**
     * Ellenőrzi a magyar sorszámszabályokat (1., 2., 5., 20.)
     */
    @Test
    public void testHungarianOrdinalCategories() {
        Locale hungarian = new Locale("hu");
        PluralRules rules = new DefaultPluralProviderSPI().load(hungarian, NumberType.ORDINALS);

        assertNotNull(rules);

        assertEquals(PluralCategory.ONE, rules.getCategory(1));
        assertEquals(PluralCategory.OTHER, rules.getCategory(2));
        assertEquals(PluralCategory.ONE, rules.getCategory(5));
        assertEquals(PluralCategory.OTHER, rules.getCategory(20));
    }

    /**
     * Ellenőrzi a walesi sorszámszabályokat (0., 1., 2., 3., ...)
     */
    @Test
    public void testWelshOrdinalCategories() {
        Locale welsh = new Locale("cy");
        PluralRules rules = new DefaultPluralProviderSPI().load(welsh, NumberType.ORDINALS);

        assertNotNull(rules);

        assertEquals(PluralCategory.ZERO, rules.getCategory(0));
        assertEquals(PluralCategory.ONE, rules.getCategory(1));
        assertEquals(PluralCategory.TWO, rules.getCategory(2));
        assertEquals(PluralCategory.FEW, rules.getCategory(3));
        assertEquals(PluralCategory.MANY, rules.getCategory(6));
        assertEquals(PluralCategory.OTHER, rules.getCategory(10));
    }
}
