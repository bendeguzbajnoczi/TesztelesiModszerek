package net.time4j.clock;

import static org.junit.Assert.*;

import net.time4j.clock.DaytimeClock;
import net.time4j.clock.NetTimeConfiguration;
import org.junit.Test;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.OutputStream;
import java.text.ParseException;

import net.time4j.Moment;
import net.time4j.scale.TimeScale;
import net.time4j.format.expert.ChronoParser;

/**
 * Ágfüggő tesztek a DaytimeClock osztályhoz.
 * A DaytimeClock a szerver Daytime protokolljára építve olvassa be a sima szöveges időpontot,
 * és azt parser segítségével Moment objektummá alakítja.
 */
public class DaytimeClockTest {

  @Test(expected = NullPointerException.class)
  public void constructorNullServerThrows() {
    // Ha a szerver cím null, a konstruktor rögtön NullPointerException-t dob.
    new DaytimeClock(null, (text, status, attrs) -> Moment.of(0, 0, TimeScale.POSIX));
  }

  @Test(expected = NullPointerException.class)
  public void constructorNullParserThrows() {
    // Parser nélküli példányosítás sem megengedett, szintén NPE-vel jelez hibát.
    new DaytimeClock("time.server", null);
  }

  @Test
  public void nistConstantConfigurationIsCorrect() {
    // A DaytimeClock.NIST konstans alapértelmezett konfigurációját ellenőrizzük:
    // a szerver címe, portja és kapcsolódási timeout értéke megfelel a NIST szabványnak.
    DaytimeClock nist = DaytimeClock.NIST;
    NetTimeConfiguration cfg = nist.getNetTimeConfiguration();
    assertEquals("time.nist.gov", cfg.getTimeServerAddress());
    assertEquals(13, cfg.getTimeServerPort());
    assertEquals(NetTimeConfiguration.DEFAULT_CONNECTION_TIMEOUT, cfg.getConnectionTimeout());
    assertEquals(0, cfg.getClockShiftWindow());
  }

  @Test
  public void getConfigurationTypeReturnsInterface() {
    // Ellenőrizzük, hogy a getConfigurationType mindig a megfelelő interfész osztályt adja vissza.
    assertEquals(NetTimeConfiguration.class, DaytimeClock.NIST.getConfigurationType());
  }

  @Test
  public void simpleConfigToStringHasExpectedFormat() {
    // Egy tetszőleges szervercímhez létrehozott DaytimeClock konfiguráció toString-je
    // előre megszövegezett formátumban jelenik meg.
    DaytimeClock clock = new DaytimeClock("foo.example", (t, s, a) -> Moment.of(0, 0, TimeScale.POSIX));
    String repr = clock.getNetTimeConfiguration().toString();
    assertEquals("SimpleDaytimeConfiguration:[server=foo.example,port=13]", repr);
  }

  @Test(expected = IOException.class)
  public void getRawTimestampThrowsOnConnectionFailure() throws IOException {
    // Ha a port 0 (érvénytelen) miatt nem lehet socketet nyitni,
    // a getRawTimestamp metódusnak IOException-t kell jeleznie.
    DaytimeClock clock = new DaytimeClock("ignored",
            (text, status, attrs) -> Moment.of(1, 0, TimeScale.POSIX)) {
      @Override public NetTimeConfiguration getNetTimeConfiguration() {
        return new NetTimeConfiguration() {
          @Override public String getTimeServerAddress() { return "localhost"; }
          @Override public int getTimeServerPort() { return 0; }
          @Override public int getConnectionTimeout() { return 1; }
          @Override public int getClockShiftWindow() { return 0; }
        };
      }
    };
    clock.getRawTimestamp();
  }

  @Test
  public void getRawTimestampReadsFromSocket() throws IOException {
    // Éles környezetet szimulálva megnyitunk egy ServerSocket-et,
    // amin egy rövid szöveget küldünk, és ellenőrizzük, hogy a getRawTimestamp pontosan azt adja vissza.
    try (ServerSocket ss = new ServerSocket(0)) {
      int port = ss.getLocalPort();
      Thread serverThread = new Thread(() -> {
        try (Socket socket = ss.accept(); OutputStream os = socket.getOutputStream()) {
          os.write("hello\n".getBytes("UTF-8"));
        } catch (IOException ignored) { }
      });
      serverThread.setDaemon(true);
      serverThread.start();

      DaytimeClock clock = new DaytimeClock("ignored",
              (text, status, attrs) -> Moment.of(1, 0, TimeScale.POSIX)) {
        @Override public NetTimeConfiguration getNetTimeConfiguration() {
          return new NetTimeConfiguration() {
            @Override public String getTimeServerAddress() { return "localhost"; }
            @Override public int getTimeServerPort() { return port; }
            @Override public int getConnectionTimeout() { return 1; }
            @Override public int getClockShiftWindow() { return 0; }
          };
        }
      };

      String raw = clock.getRawTimestamp();
      assertEquals("hello\n", raw);
    }
  }

  @Test(expected = IOException.class)
  public void connectThrowsOnIoError() throws IOException {
    // Ha a connect() során nem tudunk socketet nyitni vagy adatot olvasni, IOException-t kapunk.
    DaytimeClock clock = new DaytimeClock("ignored",
            (text, status, attrs) -> Moment.of(0, 0, TimeScale.POSIX)) {
      @Override public NetTimeConfiguration getNetTimeConfiguration() {
        return new NetTimeConfiguration() {
          @Override public String getTimeServerAddress() { return "localhost"; }
          @Override public int getTimeServerPort() { return 0; }
          @Override public int getConnectionTimeout() { return 1; }
          @Override public int getClockShiftWindow() { return 0; }
        };
      }
    };
    clock.connect();
  }

  @Test
  public void doConnectParsesTimestampCorrectly() throws IOException, ParseException {
    // A doConnect metódus kimenetének teszteléséhez egy egyszerű szervert indítunk,
    // ami egy sablonos szöveget küld, majd a ChronoParser által visszaadott Moment értéket ellenőrizzük.
    try (ServerSocket ss = new ServerSocket(0)) {
      int port = ss.getLocalPort();
      Thread serverThread = new Thread(() -> {
        try (Socket socket = ss.accept(); OutputStream os = socket.getOutputStream()) {
          os.write("timestamp\n".getBytes("UTF-8"));
        } catch (IOException ignored) { }
      });
      serverThread.setDaemon(true);
      serverThread.start();

      long posixAfter1972 = 63_072_001L;
      int nanoValue = 123;
      ChronoParser<Moment> parser = (text, status, attrs) -> Moment.of(posixAfter1972, nanoValue, TimeScale.POSIX);

      DaytimeClock clock = new DaytimeClock("ignored", parser) {
        @Override public NetTimeConfiguration getNetTimeConfiguration() {
          return new NetTimeConfiguration() {
            @Override public String getTimeServerAddress() { return "localhost"; }
            @Override public int getTimeServerPort() { return port; }
            @Override public int getConnectionTimeout() { return 1; }
            @Override public int getClockShiftWindow() { return 0; }
          };
        }
      };

      // A doConnect metódus invokálása és a visszakapott Moment ellenőrzése
      Moment result = ((DaytimeClock) clock).doConnect();
      assertEquals(posixAfter1972, result.getPosixTime());
      assertEquals(nanoValue, result.getNanosecond());
    }
  }
}