package net.time4j.clock;

import static org.junit.Assert.*;

import net.time4j.clock.FixedClock;
import org.junit.Test;

import net.time4j.Moment;
import net.time4j.base.UnixTime;

/**
 * Ágfüggő tesztek a FixedClock osztályhoz.
 * A FixedClock egy megadott UnixTime alapján hoz létre egy rögzített Moment-et,
 * és azt tárolja, továbbá implementálja az equals/hashCode/toString metódusokat.
 */
public class FixedClockTest {

  @Test(expected = NullPointerException.class)
  public void ofNullUnixTimeThrowsException() {
    // Ha null értékkel hívjuk az of() metódust, NPE-t kell kapnunk
    FixedClock.of((UnixTime) null);
  }

  @Test
  public void ofReturnsClockWithCorrectMoment() {
    // Egy egyedi UnixTime implementációval ellenőrizzük,
    // hogy a FixedClock pontosan azt a Moment-et adja vissza
    UnixTime ut = new UnixTime() {
      @Override public long getPosixTime() { return 123L; }
      @Override public int getNanosecond() { return 456; }
    };
    FixedClock clock = FixedClock.of(ut);
    Moment m = clock.currentTime();
    assertEquals(123L, m.getPosixTime());
    assertEquals(456, m.getNanosecond());
  }

  @Test
  public void ofAlwaysCreatesIndependentInstances() {
    // Az of() metódus minden híváskor új FixedClock objektumot készít,
    // azonos UnixTime alapján is, de az equals() szerint ezek egyenlőek
    UnixTime ut = new UnixTime() {
      @Override public long getPosixTime() { return 10L; }
      @Override public int getNanosecond() { return 20; }
    };
    FixedClock first = FixedClock.of(ut);
    FixedClock second = FixedClock.of(ut);

    assertNotSame(first, second);
    assertEquals(first, second);
  }

  @Test
  public void currentTimeAlwaysReturnsSameMomentInstance() {
    // A FixedClock-ben tárolt Moment-et többszöri hívásra is ugyanazzal az objektummal adja vissza
    UnixTime ut = new UnixTime() {
      @Override public long getPosixTime() { return 999L; }
      @Override public int getNanosecond() { return 888; }
    };
    FixedClock clock = FixedClock.of(ut);

    Moment first = clock.currentTime();
    Moment second = clock.currentTime();

    assertSame(first, second);
    assertEquals(999L, first.getPosixTime());
    assertEquals(888, first.getNanosecond());
  }

  @Test
  public void equalsAndHashCodeBehavior() {
    // Két különböző UnixTime implementáció, amelyek azonos adatot szolgáltatnak,
    // egyenlő FixedClock objektumokat eredményeznek és azonos hashCode-ot adnak
    UnixTime ut1 = new UnixTime() {
      @Override public long getPosixTime() { return 1L; }
      @Override public int getNanosecond() { return 2; }
    };
    UnixTime ut2 = new UnixTime() {
      @Override public long getPosixTime() { return 1L; }
      @Override public int getNanosecond() { return 2; }
    };
    UnixTime ut3 = new UnixTime() {
      @Override public long getPosixTime() { return 3L; }
      @Override public int getNanosecond() { return 4; }
    };

    FixedClock a = FixedClock.of(ut1);
    FixedClock b = FixedClock.of(ut2);
    FixedClock c = FixedClock.of(ut3);

    // Önmagával való egyenlőség
    assertTrue(a.equals(a));
    // Azonos értékek → equals true és hashCode megegyezik
    assertTrue(a.equals(b));
    assertTrue(b.equals(a));
    assertEquals(a.hashCode(), b.hashCode());
    // Különböző értékek → equals false
    assertFalse(a.equals(c));
    // Null és más típus ellenőrzése
    assertFalse(a.equals(null));
    assertFalse(a.equals("not a clock"));
  }

  @Test
  public void toStringShowsMomentDetails() {
    // A toString() formátuma FixedClock[moment=<moment.toString()>]
    UnixTime ut = new UnixTime() {
      @Override public long getPosixTime() { return 42L; }
      @Override public int getNanosecond() { return 7; }
    };
    FixedClock clock = FixedClock.of(ut);
    String expected = "FixedClock[moment=" + clock.currentTime().toString() + "]";
    assertEquals(expected, clock.toString());
  }
}
