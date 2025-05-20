package net.time4j.format;

import org.junit.Test;
import java.util.Locale;
import static org.junit.Assert.*;
import net.time4j.format.DefaultPluralProviderSPI;
import net.time4j.format.PluralCategory;

/**
 * A DefaultPluralProviderSPI osztályon belüli StdCardinalRules belső osztály viselkedésének vizsgálata
 * Ez az osztály a tőszámnevek kezelését végzi el
 */
public class StdCardinalRulesTest {
    /**
     * Alap típusellenőrzés a getter metódus segítségével
     */
    @Test
    public void testNumberType() {
        PluralRules rules = new DefaultPluralProviderSPI().load(Locale.ENGLISH, NumberType.CARDINALS);
        assertEquals(NumberType.CARDINALS, rules.getNumberType());
    }

    /**
     * Ellenőrzi az angol nyelv tőszámneveit (0, 1, 2, 11)
     */
    @Test
    public void testEnglishPluralCategories() {
        PluralRules rules = new DefaultPluralProviderSPI().load(Locale.ENGLISH, NumberType.CARDINALS);
        assertEquals(PluralCategory.OTHER, rules.getCategory(0));
        assertEquals(PluralCategory.ONE, rules.getCategory(1));
        assertEquals(PluralCategory.OTHER, rules.getCategory(2));
        assertEquals(PluralCategory.OTHER, rules.getCategory(11));
    }

    /**
     * Ellenőrzi a francia nyelv tőszámneveit (0, 1, 2, 11)
     */
    @Test
    public void testFrenchPluralCategories() {
        PluralRules rules = new DefaultPluralProviderSPI().load(Locale.FRENCH, NumberType.CARDINALS);
        assertEquals(PluralCategory.ONE, rules.getCategory(0));
        assertEquals(PluralCategory.ONE, rules.getCategory(1));
        assertEquals(PluralCategory.OTHER, rules.getCategory(2));
        assertEquals(PluralCategory.OTHER, rules.getCategory(11));
    }

    /**
     * Ellenőrzi az orosz nyelv tőszámneveit (0, 1, 2, 4, 5, 11, 14, 21)
     */
    @Test
    public void testRussianPluralCategories() {
        PluralRules rules = new DefaultPluralProviderSPI().load(new Locale("ru"), NumberType.CARDINALS);

        assertEquals(PluralCategory.ONE, rules.getCategory(1));
        assertEquals(PluralCategory.FEW, rules.getCategory(2));
        assertEquals(PluralCategory.FEW, rules.getCategory(4));
        assertEquals(PluralCategory.MANY, rules.getCategory(5));
        assertEquals(PluralCategory.MANY, rules.getCategory(11));
        assertEquals(PluralCategory.MANY, rules.getCategory(14));
        assertEquals(PluralCategory.ONE, rules.getCategory(21)); // 21 mod 10 == 1, 21 mod 100 != 11
    }

    /**
     * Ellenőrzi a szlovák nyelv tőszámneveit (0, 1, 2, 4, 5, 11)
     */
    @Test
    public void testSlovakPluralCategories() {
        PluralRules rules = new DefaultPluralProviderSPI().load(new Locale("sk"), NumberType.CARDINALS);

        assertEquals(PluralCategory.OTHER, rules.getCategory(0));
        assertEquals(PluralCategory.ONE, rules.getCategory(1));
        assertEquals(PluralCategory.FEW, rules.getCategory(2));
        assertEquals(PluralCategory.FEW, rules.getCategory(4));
        assertEquals(PluralCategory.OTHER, rules.getCategory(5));
        assertEquals(PluralCategory.OTHER, rules.getCategory(11));
    }
}
