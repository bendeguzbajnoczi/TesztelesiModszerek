package net.time4j.clock;

import static org.junit.Assert.*;

import net.time4j.clock.AdjustableClock;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import net.time4j.Moment;
import net.time4j.SystemClock;
import net.time4j.scale.TimeScale;
import net.time4j.base.TimeSource;
import net.time4j.base.UnixTime;

/**
 * Ágfüggő tesztek az AdjustableClock viselkedéséhez.
 * Itt az of(), withOffset(), withPulse() és currentTime() különböző ágait
 * és a equals()/hashCode()/toString() helyességét ellenőrizzük.
 */
public class AdjustableClockTest {

  /**
   * Ellenőrzi, hogy rendszeróra alapú forrásból készített példány
   * azonos singleton objektumot ad vissza.
   */
  @Test
  public void testOf_withSystemClock() {
    AdjustableClock clock = AdjustableClock.of(SystemClock.INSTANCE);
    assertNotNull(clock);
    assertSame(AdjustableClock.ofSystem(), clock);
  }

  /**
   * Egyedi TimeSource használatakor új, de helyes példány jön létre,
   * ami nem azonos a rendszeróra-alapú singleton-nal.
   */
  @Test
  public void testOf_withCustomClock() {
    TimeSource<UnixTime> customSource = new TimeSource<UnixTime>() {
      @Override public UnixTime currentTime() {
        return new UnixTime() {
          @Override public long getPosixTime() { return 42L; }
          @Override public int getNanosecond() { return 0; }
        };
      }
    };
    AdjustableClock clock = AdjustableClock.of(customSource);
    assertNotNull(clock);
    assertNotSame(AdjustableClock.ofSystem(), clock);
  }

  /**
   * null értékű átütés egység (pulse) átadása NPE-t eredményez.
   */
  @Test(expected = NullPointerException.class)
  public void testWithPulse_nullPulse() {
    AdjustableClock.of(SystemClock.INSTANCE).withPulse(null);
  }

  /**
   * Érvényes pulse beállítása a példány létező objektumot ad vissza.
   */
  @Test
  public void testWithPulse_nonNullPulse() {
    AdjustableClock clock = AdjustableClock.of(SystemClock.INSTANCE)
            .withPulse(TimeUnit.MILLISECONDS);
    assertNotNull(clock);
  }

  /**
   * ofSystem() és of(SystemClock.INSTANCE) mindig ugyanazt a példányt adják.
   */
  @Test
  public void testOfSystemReturnsSingleton() {
    AdjustableClock s1 = AdjustableClock.ofSystem();
    AdjustableClock s2 = AdjustableClock.of(SystemClock.INSTANCE);
    assertSame(s1, s2);
  }

  /**
   * Ha meglévő AdjustableClock példányt adunk át az of()-nak,
   * azonos objektumot kapunk vissza.
   */
  @Test
  public void testOfWithAdjustableClockReturnsSame() {
    AdjustableClock original = AdjustableClock.ofSystem()
            .withOffset(0, TimeUnit.NANOSECONDS);
    AdjustableClock result = AdjustableClock.of(original);
    assertSame(original, result);
  }

  /**
   * Két külön hívás ugyanazzal az új TimeSource-forrással
   * különböző példányokat, de equals() szerint egyenlő objektumokat ad.
   */
  @Test
  public void testOfWithNewSourceCreatesDistinctEqualInstances() {
    TimeSource<Moment> src = new TimeSource<Moment>() {
      @Override public Moment currentTime() {
        return Moment.of(200L, 500_000_000, TimeScale.POSIX);
      }
      @Override public String toString() { return "FixedSource"; }
    };
    AdjustableClock c1 = AdjustableClock.of(src);
    AdjustableClock c2 = AdjustableClock.of(src);
    assertNotSame(c1, c2);
    assertEquals(c1, c2);
  }

  /**
   * null offset egység (TimeUnit) átadása NullPointerException-t dob.
   */
  @Test(expected = NullPointerException.class)
  public void testWithOffsetNullUnitThrows() {
    AdjustableClock.ofSystem().withOffset(1, null);
  }

  /**
   * nullás offset beállítása (0, NANOSECONDS) nem hoz létre új példányt,
   * hanem visszaadja a már meglévő objektumot.
   */
  @Test
  public void testWithOffsetSameValuesReturnsThis() {
    AdjustableClock base = AdjustableClock.ofSystem();
    AdjustableClock same = base.withOffset(0, TimeUnit.NANOSECONDS);
    assertSame(base, same);
  }

  /**
   * Nem-nullás offset beállításakor új példány jön létre,
   * és a currentTime() az offsetelt időt adja.
   */
  @Test
  public void testWithOffsetDifferentValuesAdjustsTimeAndReturnsNew() {
    TimeSource<Moment> src = () -> Moment.of(100L, 0, TimeScale.POSIX);
    AdjustableClock base = AdjustableClock.of(src);
    AdjustableClock adjusted = base.withOffset(5, TimeUnit.SECONDS);
    assertNotSame(base, adjusted);
    Moment m = adjusted.currentTime();
    assertEquals(105L, m.getPosixTime());
    assertEquals(0, m.getNanosecond());
  }

  /**
   * A currentTime() viselkedése a pulse beállításoktól függően:
   * - SECONDS: csak egész másodperc
   * - MINUTES: a másodpercérték lekerekítve percre
   * - NANOSECONDS: teljes pontosság megtartva
   */
  @Test
  public void testCurrentTimeSwitchCases() {
    TimeSource<Moment> src = () -> Moment.of(3601L, 1234, TimeScale.POSIX);

    AdjustableClock secClock = AdjustableClock.of(src).withPulse(TimeUnit.SECONDS);
    Moment secMoment = secClock.currentTime();
    assertEquals(3601L, secMoment.getPosixTime());
    assertEquals(0, secMoment.getNanosecond());

    AdjustableClock minClock = AdjustableClock.of(src).withPulse(TimeUnit.MINUTES);
    Moment minMoment = minClock.currentTime();
    assertEquals(3600L, minMoment.getPosixTime());
    assertEquals(0, minMoment.getNanosecond());

    AdjustableClock nanoClock = AdjustableClock.of(src).withPulse(TimeUnit.NANOSECONDS);
    Moment nanoMoment = nanoClock.currentTime();
    assertEquals(3601L, nanoMoment.getPosixTime());
    assertEquals(1234, nanoMoment.getNanosecond());
  }

  /**
   * equals(), hashCode() és toString() helyes működésének ellenőrzése:
   * azonos konfiguráció esetén equals true, hashCode egyezik,
   * toString tartalmazza a forrás nevét, offset és pulse beállításokat.
   */
  @Test
  public void testEqualsHashCodeAndToString() {
    TimeSource<Moment> src = new TimeSource<Moment>() {
      @Override public Moment currentTime() {
        return Moment.of(0L, 0, TimeScale.POSIX);
      }
      @Override public String toString() { return "MySource"; }
    };
    AdjustableClock c1 = AdjustableClock.of(src)
            .withOffset(3, TimeUnit.HOURS)
            .withPulse(TimeUnit.DAYS);
    AdjustableClock c2 = AdjustableClock.of(src)
            .withOffset(3, TimeUnit.HOURS)
            .withPulse(TimeUnit.DAYS);

    assertTrue(c1.equals(c1));
    assertTrue(c1.equals(c2));
    assertEquals(c1.hashCode(), c2.hashCode());
    assertFalse(c1.equals(null));
    assertFalse(c1.equals("foo"));

    String s = c1.toString();
    assertTrue(s.startsWith("AdjustableClock["));
    assertTrue(s.contains("source=MySource"));
    assertTrue(s.contains("offset-amount=3"));
    assertTrue(s.contains("offset-unit=HOURS"));
    assertTrue(s.contains("pulse=DAYS"));
    assertTrue(s.endsWith("]"));
  }
}
