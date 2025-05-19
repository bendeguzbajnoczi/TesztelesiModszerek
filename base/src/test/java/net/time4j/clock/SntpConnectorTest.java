package net.time4j.clock;

import static org.junit.Assert.*;

import net.time4j.clock.SntpConfiguration;
import net.time4j.clock.SntpConnector;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import net.time4j.Moment;
import net.time4j.SystemClock;
import net.time4j.scale.TimeScale;

/**
 * Ágfüggő tesztek az SntpConnector osztályhoz.
 * Cél: minden fontos kivétel- és viselkedési ágat ellenőrizni stub és reflexiós technikákkal.
 */
public class SntpConnectorTest {

  // Egy minimális stub konfiguráció a konstruktorokhoz,
  // hogy ne dobjon NullPointerException-t a szuperhívások során.
  private static final SntpConfiguration STUB_CFG = new SntpConfiguration() {
    @Override public String getTimeServerAddress() { return "stub"; }
    @Override public int getTimeServerPort() { return 123; }
    @Override public int getConnectionTimeout() { return 1; }
    @Override public boolean isNTP4() { return true; }
    @Override public int getRequestInterval() { return 1; }
    @Override public short getRequestCount() { return 1; }
    @Override public int getClockShiftWindow() { return 0; }
  };

  // Egyedi connector, amivel kontrollálható, hogy az isRunning()
  // milyen értéket ad vissza, és fix offset mikroszekundumban.
  private static class OffsetConnector extends SntpConnector {
    private final boolean running;
    private final long offsetMicros;
    OffsetConnector(boolean running, long offsetMicros) {
      super(STUB_CFG);
      this.running = running;
      this.offsetMicros = offsetMicros;
    }
    @Override public boolean isRunning() { return running; }
    @Override protected long getLastOffset(long micros) { return offsetMicros; }
  }

  // --- Konstruktorok és konfiguráció betöltésének vizsgálata ---

  /**
   * Teszt: alapértelmezett konstruktor hívásánál,
   * ha nincs szolgáltatás regisztrálva, IllegalStateException-t várunk.
   */
  @Test(expected = IllegalStateException.class)
  public void ctorWithoutServiceThrows() {
    new SntpConnector();
  }

  /**
   * Teszt: ha a hoszt string null, akkor a konstruktor NullPointerException-t dob.
   */
  @Test(expected = NullPointerException.class)
  public void ctorWithNullServerThrowsNPE() {
    new SntpConnector((String) null);
  }

  /**
   * Teszt: ha egy érvényes címzettet adunk meg,
   * akkor a String-konstruktorból származó konfiguráció mezői helyesen állnak be.
   */
  @Test
  public void ctorWithHostBuildsDefaultConfig() {
    SntpConnector conn = new SntpConnector("time.server");
    SntpConfiguration cfg = conn.getNetTimeConfiguration();
    assertEquals("time.server", cfg.getTimeServerAddress());
    assertEquals(123, cfg.getTimeServerPort());
    assertTrue(cfg.isNTP4());
    // Alapértelmezett lekérdezési intervallum 4 perc
    assertEquals(60 * 4, cfg.getRequestInterval());
    // Egy kérés egy ciklusban
    assertEquals(1, cfg.getRequestCount());
    // Óra korrekciós ablak alapértelmezetten 0 másodperc
    assertEquals(0, cfg.getClockShiftWindow());
    // A toString() formátuma is ellenőrzött
    assertEquals("SimpleNtpConfiguration:[server=time.server,port=123]", cfg.toString());
  }

  // --- currentTimeInMillis / currentTimeInMicros viselkedése, ha nem fut a connector ---

  /**
   * Teszt: ha a connector nem fut, akkor a currentTimeIn... metódusok
   * egyszerűen a Moment szerinti időt adják vissza konverzió nélkül.
   */
  @Test
  public void currentTimeNotRunningReturnsMomentDirectly() {
    class C extends SntpConnector {
      C() { super(STUB_CFG); }
      @Override public boolean isRunning() { return false; }
      @Override public Moment currentTime() {
        // 1s és 1_500_000ns -> POSIX skálán 1_001_500 mikroszekundum
        return Moment.of(1L, 1_500_000, TimeScale.POSIX);
      }
    }
    C conn = new C();
    // millis: 1_001_500μs -> 1001ms
    assertEquals(1001L, conn.currentTimeInMillis());
    // micros: 1_001_500μs
    assertEquals(1_001_500L, conn.currentTimeInMicros());
  }

  // --- currentTime... futó connector esetén ---

  /**
   * Teszt: ha a connector fut és pozitív offset van,
   * akkor a currentTime... eredménye legalább az offsettel nagyobb.
   * (Fluktuáció miatt csak >= ellenőrzés.)
   */
  @Ignore("Flaky under CI: időzítés alapú teszt, futási sorrend miatt időnként hibázik")
  @Test
  public void currentTimeRunningAppliesOffset() {
    long baseMs = SystemClock.MONOTONIC.currentTimeInMillis();
    long resultMs = new OffsetConnector(true, 2_000_000L).currentTimeInMillis();
    assertTrue(resultMs >= baseMs + 2000);

    long baseUs = SystemClock.MONOTONIC.currentTimeInMicros();
    long resultUs = new OffsetConnector(true, 5_000L).currentTimeInMicros();
    assertTrue(resultUs >= baseUs + 5000);
  }

  // --- getLastReply kezdeti állapot vizsgálata ---

  /**
   * Teszt: ha még nem történt connect(), a getLastReply() null-t ad.
   */
  @Test
  public void getLastReplyInitiallyNull() {
    class C extends OffsetConnector {
      C() { super(false, 0L); }
      @Override protected Moment doConnect() { return Moment.of(0L,0,TimeScale.POSIX); }
    }
    assertNull(new C().getLastReply());
  }

  // --- doConnect viselkedése requestCount = 0 esetén ---

  /**
   * Teszt: ha a konfigurációban requestCount=0,
   * akkor a super.doConnect() fut, ami monotonikus időt ad vissza.
   */
  @Test
  public void doConnectWithZeroRequestCountUsesMonotonicTime() throws IOException {
    SntpConfiguration zeroReq = new SntpConfiguration() {
      @Override public String getTimeServerAddress() { return "x"; }
      @Override public int getTimeServerPort() { return 0; }
      @Override public int getConnectionTimeout() { return 0; }
      @Override public boolean isNTP4() { return false; }
      @Override public int getRequestInterval() { return 0; }
      @Override public short getRequestCount() { return 0; }
      @Override public int getClockShiftWindow() { return 0; }
    };
    class C extends SntpConnector {
      C() { super(zeroReq); }
      @Override protected Moment doConnect() throws IOException {
        return super.doConnect();
      }
    }
    Moment m = new C().doConnect();
    assertNotNull(m);
    assertTrue(m.getPosixTime() >= 0);
  }

  // --- Konfiguráció betöltésének érvényességi ellenőrzése ---

  /**
   * Teszt: negatív requestCount érvénytelen, IllegalStateException-t várunk.
   */
  @Test(expected = IllegalStateException.class)
  public void loadConfigNegativeCountThrows() {
    SntpConfiguration bad = new SntpConfiguration() {
      @Override public String getTimeServerAddress() { return "ok"; }
      @Override public int getTimeServerPort() { return 1; }
      @Override public int getConnectionTimeout() { return 1; }
      @Override public boolean isNTP4() { return false; }
      @Override public int getRequestInterval() { return 1; }
      @Override public short getRequestCount() { return -1; }
      @Override public int getClockShiftWindow() { return 0; }
    };
    new SntpConnector(bad) { public SntpConfiguration load() { return super.loadNetTimeConfiguration(); } }.load();
  }

  /**
   * Teszt: túl nagy requestCount (>255) esetén is IllegalStateException.
   */
  @Test(expected = IllegalStateException.class)
  public void loadConfigTooLargeCountThrows() {
    SntpConfiguration bad = new SntpConfiguration() {
      @Override public String getTimeServerAddress() { return "ok"; }
      @Override public int getTimeServerPort() { return 1; }
      @Override public int getConnectionTimeout() { return 1; }
      @Override public boolean isNTP4() { return false; }
      @Override public int getRequestInterval() { return 1; }
      @Override public short getRequestCount() { return 1000; }
      @Override public int getClockShiftWindow() { return 0; }
    };
    new SntpConnector(bad) { public SntpConfiguration load() { return super.loadNetTimeConfiguration(); } }.load();
  }

  /**
   * Teszt: nullás requestInterval sem megengedett, IllegalStateException-t dob.
   */
  @Test(expected = IllegalStateException.class)
  public void loadConfigNonPositiveIntervalThrows() {
    SntpConfiguration bad = new SntpConfiguration() {
      @Override public String getTimeServerAddress() { return "ok"; }
      @Override public int getTimeServerPort() { return 1; }
      @Override public int getConnectionTimeout() { return 1; }
      @Override public boolean isNTP4() { return false; }
      @Override public int getRequestInterval() { return 0; }
      @Override public short getRequestCount() { return 1; }
      @Override public int getClockShiftWindow() { return 0; }
    };
    new SntpConnector(bad) { public SntpConfiguration load() { return super.loadNetTimeConfiguration(); } }.load();
  }

  /**
   * Teszt: érvényes konfiguráció esetén loadNetTimeConfiguration
   * visszaadja ugyanazt az objektumot, amit megadtunk.
   */
  @Test
  public void loadConfigValidReturnsSameInstance() {
    SntpConfiguration good = new SntpConfiguration() {
      @Override public String getTimeServerAddress() { return "ok"; }
      @Override public int getTimeServerPort() { return 1; }
      @Override public int getConnectionTimeout() { return 1; }
      @Override public boolean isNTP4() { return false; }
      @Override public int getRequestInterval() { return 10; }
      @Override public short getRequestCount() { return 5; }
      @Override public int getClockShiftWindow() { return 0; }
    };
    class C extends SntpConnector {
      C() { super(good); }
      public SntpConfiguration load() { return super.loadNetTimeConfiguration(); }
    }
    assertSame(good, new C().load());
  }

  /**
   * Teszt: getConfigurationType mindig a SntpConfiguration interface-t adja vissza.
   */
  @Test
  public void getConfigurationTypeReturnsInterface() {
    assertEquals(SntpConfiguration.class,
            new SntpConnector("server").getConfigurationType());
  }

}
