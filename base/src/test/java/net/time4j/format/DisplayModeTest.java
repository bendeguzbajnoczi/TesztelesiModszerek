package net.time4j.format;

import static org.junit.Assert.*;
import org.junit.Test;
import java.text.DateFormat;

/**
 * Tesztosztály a DisplayMode enum teljes ágakövetéséhez.
 * Ellenőrzi a getStyleValue() és az ofStyle(int) metódusok helyes működését.
 */
public class DisplayModeTest {

  /**
   * Teszt: FULL.getStyleValue() visszaadja a DateFormat.FULL értéket (0).
   */
  @Test
  public void testGetStyleValueFull() {
    assertEquals("FULL stilusnak a 0 értéket kell visszaadnia",
            DateFormat.FULL,
            DisplayMode.FULL.getStyleValue());
  }

  /**
   * Teszt: LONG.getStyleValue() visszaadja a DateFormat.LONG értéket (1).
   */
  @Test
  public void testGetStyleValueLong() {
    assertEquals("LONG stilusnak az 1 értéket kell visszaadnia",
            DateFormat.LONG,
            DisplayMode.LONG.getStyleValue());
  }

  /**
   * Teszt: MEDIUM.getStyleValue() visszaadja a DateFormat.MEDIUM értéket (2).
   */
  @Test
  public void testGetStyleValueMedium() {
    assertEquals("MEDIUM stilusnak a 2 értéket kell visszaadnia",
            DateFormat.MEDIUM,
            DisplayMode.MEDIUM.getStyleValue());
  }

  /**
   * Teszt: SHORT.getStyleValue() visszaadja a DateFormat.SHORT értéket (3).
   */
  @Test
  public void testGetStyleValueShort() {
    assertEquals("SHORT stilusnak a 3 értéket kell visszaadnia",
            DateFormat.SHORT,
            DisplayMode.SHORT.getStyleValue());
  }

  /**
   * Teszt: ofStyle(DateFormat.FULL) visszaadja a FULL enum-értéket.
   */
  @Test
  public void testOfStyleFull() {
    assertSame("A 0 stílus FULL-t kell hogy adja vissza",
            DisplayMode.FULL,
            DisplayMode.ofStyle(DateFormat.FULL));
  }

  /**
   * Teszt: ofStyle(DateFormat.LONG) visszaadja a LONG enum-értéket.
   */
  @Test
  public void testOfStyleLong() {
    assertSame("Az 1 stílus LONG-t kell hogy adja vissza",
            DisplayMode.LONG,
            DisplayMode.ofStyle(DateFormat.LONG));
  }

  /**
   * Teszt: ofStyle(DateFormat.MEDIUM) visszaadja a MEDIUM enum-értéket.
   */
  @Test
  public void testOfStyleMedium() {
    assertSame("A 2 stílus MEDIUM-ot kell hogy adja vissza",
            DisplayMode.MEDIUM,
            DisplayMode.ofStyle(DateFormat.MEDIUM));
  }

  /**
   * Teszt: ofStyle(DateFormat.SHORT) visszaadja a SHORT enum-értéket.
   */
  @Test
  public void testOfStyleShort() {
    assertSame("A 3 stílus SHORT-ot kell hogy adja vissza",
            DisplayMode.SHORT,
            DisplayMode.ofStyle(DateFormat.SHORT));
  }

  /**
   * Teszt, hogy ofStyle keresés után nem található stílus esetén
   * UnsupportedOperationException-t dob, és az üzenet tartalmazza a nem támogatott értéket.
   */
  @Test
  public void testOfStyleInvalidThrows() {
    int invalid = -5;
    try {
      DisplayMode.ofStyle(invalid);
      fail("-5 nem támogatott stílus: UnsupportedOperationException-t vártunk");
    } catch (UnsupportedOperationException ex) {
      assertEquals(
              "Az exception üzenetnek pontosan kell lennie",
              "Unknown format style: -5",
              ex.getMessage()
      );
    }
  }

  /**
   * Teszt, hogy más nem érvényes stílus is hibát okoz (pl. 99).
   */
  @Test(expected = UnsupportedOperationException.class)
  public void testOfStyleAnotherInvalid() {
    DisplayMode.ofStyle(99);
  }

}
