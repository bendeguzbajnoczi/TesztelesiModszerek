package net.time4j.clock;

import static org.junit.Assert.*;

import net.time4j.clock.HttpClock;
import net.time4j.clock.NetTimeConfiguration;
import org.junit.Test;

import java.io.IOException;
import java.net.InetSocketAddress;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import net.time4j.Moment;

/**
 * Ágfüggő tesztek az HttpClock osztályhoz.
 * Cél: a konstruktor viselkedését és a HTTP-alapú időszinkronizálás fő ágait ellenőrizni.
 * A tesztek beágyazott HttpServer segítségével szimulálják a Date fejlécű válaszokat,
 * és ellenőrzik a hibakezelést rossz státusz- és formátumú válaszok esetén.
 */
public class HttpClockTest {

  /**
   * Ha a konstruktornak null argumentumot adunk,
   * a SimpleHttpConfiguration dob NullPointerException-t.
   */
  @Test(expected = NullPointerException.class)
  public void constructorWithNullServerThrowsNPE() {
    new HttpClock(null);
  }

  /**
   * Ellenőrzi a SimpleHttpConfiguration alapértelmezett beállításait:
   * - ha nincs "http(s)://" előtag, automatikusan hozzáadódik a http:// prefix
   * - a port a protokollnak megfelelő (http→80, https→443)
   * - a toString formátuma
   */
  @Test
  public void simpleHttpConfigurationDefaults() {
    // HTTP (prefix nélkül) → http://example.com:80
    HttpClock clock1 = new HttpClock("example.com");
    NetTimeConfiguration cfg1 = clock1.getNetTimeConfiguration();
    assertEquals("http://example.com", cfg1.getTimeServerAddress());
    assertEquals(80, cfg1.getTimeServerPort());
    assertEquals(NetTimeConfiguration.DEFAULT_CONNECTION_TIMEOUT, cfg1.getConnectionTimeout());
    assertEquals(0, cfg1.getClockShiftWindow());
    assertEquals("SimpleHttpConfiguration:[server=http://example.com,port=80]", cfg1.toString());

    // Explicit "http://" prefix megtartása
    HttpClock clock2 = new HttpClock("http://foo.com");
    NetTimeConfiguration cfg2 = clock2.getNetTimeConfiguration();
    assertEquals("http://foo.com", cfg2.getTimeServerAddress());
    assertEquals(80, cfg2.getTimeServerPort());
    assertEquals("SimpleHttpConfiguration:[server=http://foo.com,port=80]", cfg2.toString());

    // HTTPS prefix → port 443
    HttpClock clock3 = new HttpClock("https://secure.com");
    NetTimeConfiguration cfg3 = clock3.getNetTimeConfiguration();
    assertEquals("https://secure.com", cfg3.getTimeServerAddress());
    assertEquals(443, cfg3.getTimeServerPort());
    assertEquals("SimpleHttpConfiguration:[server=https://secure.com,port=443]", cfg3.toString());
  }

  /**
   * Szimulálunk egy HTTP szervert, ami sikeres (200) választ küld egy "Date" fejlécben:
   * a válasz 1970-01-01T00:00:01Z időpontot tartalmaz.
   * Ellenőrizzük, hogy a connect() hívás után
   * a lastConnectionTime mező frissül, és eltér a korábbi currentTime() értéktől.
   */
  @Test
  public void connectUpdatesLastConnectionTime() throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/", new HttpHandler() {
      @Override
      public void handle(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().set("Date", "Thu, 01 Jan 1970 00:00:01 GMT");
        exchange.sendResponseHeaders(200, 0);
        exchange.close();
      }
    });
    server.start();
    try {
      int port = server.getAddress().getPort();
      HttpClock clock = new HttpClock("localhost:" + port);

      // Még nem hívtuk meg connect(), getLastConnectionTime() null
      assertNull(clock.getLastConnectionTime());
      Moment before = clock.currentTime();
      assertNotNull(before);

      // Végrehajtjuk a HTTP HEAD kérést
      clock.connect();

      // Az új időpontnak nem szabad megegyeznie a start előtti idővel
      Moment after = clock.getLastConnectionTime();
      assertNotNull(after);
      assertFalse(before.equals(after));
    } finally {
      server.stop(0);
    }
  }

  /**
   * Ha a HTTP státusz nem 2xx (pl. 500), IOException-t kell dobni,
   * üzenete a HTTP státusz kóddal kell, hogy kezdődjön.
   */
  @Test
  public void connectThrowsIOExceptionOnBadStatus() throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/", exchange -> {
      exchange.sendResponseHeaders(500, 0);
      exchange.close();
    });
    server.start();
    try {
      int port = server.getAddress().getPort();
      HttpClock clock = new HttpClock("localhost:" + port);
      try {
        clock.connect();
        fail("IOException-t vártunk HTTP 500 válaszra");
      } catch (IOException e) {
        assertTrue(e.getMessage().startsWith("HTTP server status: 500"));
      }
    } finally {
      server.stop(0);
    }
  }

  /**
   * Ha a Date fejléc formátuma hibás, ne dobjon kivételt,
   * hanem állítson be a lastConnectionTime mezőbe rendszeridőt.
   */
  @Test
  public void connectHandlesMalformedDateHeader() throws Exception {
    HttpServer server = HttpServer.create(new InetSocketAddress(0), 0);
    server.createContext("/", exchange -> {
      exchange.getResponseHeaders().set("Date", "not a date");
      exchange.sendResponseHeaders(200, 0);
      exchange.close();
    });
    server.start();
    try {
      int port = server.getAddress().getPort();
      HttpClock clock = new HttpClock("localhost:" + port);

      assertNull(clock.getLastConnectionTime());
      Moment before = clock.currentTime();

      clock.connect();
      Moment after = clock.getLastConnectionTime();
      assertNotNull(after);
      assertFalse(before.equals(after));
    } finally {
      server.stop(0);
    }
  }

  /**
   * A getConfigurationType() mindig a NetTimeConfiguration osztályt adja vissza.
   */
  @Test
  public void getConfigurationTypeReturnsInterface() {
    HttpClock clock = new HttpClock("example.com");
    assertEquals(NetTimeConfiguration.class, clock.getConfigurationType());
  }
}
