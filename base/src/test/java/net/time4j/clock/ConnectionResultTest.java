package net.time4j.clock;

import static org.junit.Assert.*;
import org.junit.Test;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

import net.time4j.Moment;
import net.time4j.scale.TimeScale;

/**
 * Ágfüggő tesztek a ConnectionResult osztályhoz.
 * A ConnectionResult a szerver és a helyi idő eltérését számítja ki,
 * és egy átmeneti időablakon belül lineárisan simítja az értéket.
 * A tesztek reflexióval érik el a privát konstruktorokat, mezőket és metódusokat.
 */
public class ConnectionResultTest {

  /**
   * Privát konstruktor elérése reflexióval:
   * beállítja a szerveridő Moment-et, a helyi mikroszekundum-eltérést,
   * a kezdeti offsetet és az ablak hosszát.
   */
  private static Object createResult(Moment time, long localMicros, long startOffset, int windowSec) throws Exception {
    Class<?> crClass = Class.forName("net.time4j.clock.NetTimeConnector$ConnectionResult");
    Constructor<?> ctor = crClass.getDeclaredConstructor(Moment.class, long.class, long.class, int.class);
    ctor.setAccessible(true);
    return ctor.newInstance(time, localMicros, startOffset, windowSec);
  }

  /**
   * Privát getActualOffset metódus meghívása reflexióval:
   * különböző mikroszekundum-bemenetek esetén vizsgáljuk az eltérés visszaadását.
   */
  private static long actualOffset(Object cr, long micros) throws Exception {
    Method m = cr.getClass().getDeclaredMethod("getActualOffset", long.class);
    m.setAccessible(true);
    return (Long) m.invoke(cr, micros);
  }

  /**
   * Privát endOffset mező kiolvasása reflexióval:
   * összevetjük a metódusban használt végső eltolást a tesztelt kimenettel.
   */
  private static long readEndOffset(Object cr) throws Exception {
    Field f = cr.getClass().getDeclaredField("endOffset");
    f.setAccessible(true);
    return (Long) f.get(cr);
  }

  @Test
  public void testWindowZeroAlwaysUsesEndOffset() throws Exception {
    // Ha nincs átmeneti ablak (windowSec=0), a visszatérő érték mindig endOffset.
    Object cr = createResult(Moment.of(0L, 0, TimeScale.POSIX), 100L, 50L, 0);
    long expected = readEndOffset(cr);
    long actual = actualOffset(cr, 1_000_000L);
    assertEquals(expected, actual);
  }

  @Test
  public void testStartOffsetLessOrEqualEndOffsetUtcScale() throws Exception {
    // Ha startOffset <= endOffset, mindig endOffset-et ad vissza.
    // Itt UTC skálát használunk, extractMicros(time)=0, endOffset=0.
    Object cr = createResult(Moment.of(0L, 0, TimeScale.UTC), 0L, -1L, 1);
    long expected = readEndOffset(cr);
    assertTrue("startOffset nem kisebb vagy egyenlő endOffset-nél", -1L <= expected);
    long actual = actualOffset(cr, 100L);
    assertEquals(expected, actual);
  }

  @Test
  public void testMicrosExceedWindow() throws Exception {
    // Ha a bemeneti mikroszekundum nagyobb vagy egyenlő az ablak hosszának mikroszekundumban,
    // a metódus a simítás helyett az endOffset-et adja.
    int window = 1;
    Object cr = createResult(Moment.of(0L, 100_000, TimeScale.POSIX), 0L, 200L, window);
    long expected = readEndOffset(cr);
    long actual = actualOffset(cr, window * 1_000_000L);
    assertEquals(expected, actual);
  }

  @Test
  public void testSmoothTransitionBranch() throws Exception {
    // Ha 0 < micros < windowSec*1_000_000, lineáris interpoláció történik
    // a startOffset és az endOffset között.
    int window = 2;
    long startOffset = 200L;
    Object cr = createResult(Moment.of(0L, 100_000, TimeScale.POSIX), 0L, startOffset, window);
    long endOffset = readEndOffset(cr);
    long midMicros = window * 1_000_000L / 2;
    long actual = actualOffset(cr, midMicros);
    long min = Math.min(startOffset, endOffset);
    long max = Math.max(startOffset, endOffset);
    assertTrue("Interpolált értéknek a két végérték között kell lennie", actual > min && actual < max);
  }
}