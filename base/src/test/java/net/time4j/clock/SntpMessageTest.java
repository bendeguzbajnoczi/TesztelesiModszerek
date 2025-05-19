package net.time4j.clock;

import static org.junit.Assert.*;

import net.time4j.clock.SntpMessage;
import org.junit.Test;

import java.io.IOException;

/**
 * Ágfüggő tesztek az SntpMessage osztály feldolgozásához.
 * Teszteljük a szerver- és klienskonstruktorok helyes viselkedését,
 * az időkonverziós segédfüggvényt és a privát utility-metódus használatát.
 *
 * LI (Leap Indicator) – 2 bit
 * 0: nincs ugrómásodperc-figyelmeztetés (normal)
 * 1: nem használt (bólintásmentes átmenet)
 * 2: előre jelez egy negatív ugrómásodpercet
 * 3: az órát szinkronizálni kell (alarm condition)
 *
 * VN (Version Number) – 3 bit
 * Meghatározza, hogy az üzenet melyik NTP-verziónak felel meg.
 * VN=3 → NTPv3 (RFC 1305)
 * VN=4 → NTPv4 (RFC 5905)
 *
 * Mode – 3 bit
 * 3: Client
 * 4: Server
 * 5: Broadcast
 *
 */
public class SntpMessageTest {

  @Test(expected = IOException.class)
  public void serverConstructorThrowsWhenNoTransmitTimestamp() throws IOException {
    // Üres adat: minden byte 0 → első ellenőrzés hibát dob
    byte[] data = new byte[48];
    new SntpMessage(data, 0.0, (byte) 3);
  }

  @Test
  public void serverConstructorDetectsOriginateTimestampMismatch() {
    // header byte = LI=0, VN=3, Mode=4 → 00011100₂ = 28
    byte[] data = new byte[48];
    data[0] = 28;
    data[44] = 1; // átugorjuk az üres timestampet
    try {
      new SntpMessage(data, 0.0, (byte) 3);
      fail("Originate timestamp mismatch-et vártunk");
    } catch (IOException ex) {
      assertTrue(ex.getMessage().startsWith(
              "Originate timestamp does not match sent timestamp"
      ));
    }
  }

  @Test
  public void serverConstructorRejectsInvalidServerMode() {
    // header byte = Mode=2, VN=3 → 00011010₂ = 26
    byte[] data = new byte[48];
    data[0] = 26;
    data[44] = 1;
    try {
      new SntpMessage(data, 0.0, (byte) 3);
      fail("Unexpected server mode-ra vártunk IOException-t");
    } catch (IOException ex) {
      assertEquals("Unexpected server mode: 2", ex.getMessage());
    }
  }

  @Test
  public void serverConstructorRejectsInvalidVersion() {
    // header byte = VN=0, Mode=5 → 00000101₂ = 5
    byte[] data = new byte[48];
    data[0] = 5;
    data[44] = 1;
    try {
      new SntpMessage(data, 0.0, (byte) 4);
      fail("Unexpected ntp version-ra vártunk IOException-t");
    } catch (IOException ex) {
      assertEquals("Unexpected ntp version: 0", ex.getMessage());
    }
  }

  @Test
  public void serverConstructorRejectsOutOfRangeStratum() {
    // header byte = VN=4, Mode=5 → 00100101₂ = 37
    byte[] data = new byte[48];
    data[0] = 37;
    data[1] = (byte) 16; // stratum out-of-range (>15)
    data[44] = 1;
    try {
      new SntpMessage(data, 0.0, (byte) 4);
      fail("Unexpected stratum-ra vártunk IOException-t");
    } catch (IOException ex) {
      assertEquals("Unexpected stratum: 16", ex.getMessage());
    }
  }

  @Test
  public void clientConstructorSetsHeaderCorrectlyForMode3() {
    // client=false → VN=3, Mode=3 → 00011011₂ = 27
    SntpMessage msgFalse = new SntpMessage(false);
    assertEquals(3, msgFalse.getVersion());
    assertEquals(3, msgFalse.getMode());
    byte[] outFalse = msgFalse.getBytes();
    assertEquals(48, outFalse.length);
    assertEquals(27, outFalse[0] & 0xFF);

    // client=true → VN=4, Mode=3 → 00100011₂ = 35
    SntpMessage msgTrue = new SntpMessage(true);
    assertEquals(4, msgTrue.getVersion());
    assertEquals(3, msgTrue.getMode());
    byte[] outTrue = msgTrue.getBytes();
    assertEquals(48, outTrue.length);
    assertEquals(35, outTrue[0] & 0xFF);
  }

  @Test
  public void convertEpochAndFractionalSeconds() {
    // NTP epoch = UNIX epoch → 0 mikroszekundum
    long zero = SntpMessage.convert(2208988800.0);
    assertEquals(0L, zero);
    // +1.234567 mp → 1_234_567 μs
    double ntpWithFrac = 2208988800.0 + 1.234567;
    assertEquals(1_234_567L, SntpMessage.convert(ntpWithFrac));
  }

  @Test
  public void gettersReturnCorrectValuesForServerMessageWithCharRefID() throws IOException {
    // header byte = LI=2, VN=3, Mode=5 → 10011101₂ = 157
    byte[] data = new byte[48];
    data[0] = (byte)157;
    data[1] = 1;
    data[2] = 8;
    data[3] = (byte) -10;
    System.arraycopy("REF!".getBytes("US-ASCII"), 0, data, 12, 4);
    data[44] = 1;

    SntpMessage msg = new SntpMessage(data, -1.0, (byte) 3);
    assertEquals(2, msg.getLeapIndicator());
    assertEquals(3, msg.getVersion());
    assertEquals(5, msg.getMode());
    assertEquals(1, msg.getStratum());
    assertEquals(8, msg.getPollInterval());
    assertEquals(-10, msg.getPrecision(), 0);
    assertEquals("REF!", msg.getReferenceIdentifier());
  }

  @Test
  public void getBytesReflectServerMessageHeaderAndRefID() throws IOException {
    byte[] data = new byte[48];
    data[0] = 37; // VN=4, Mode=5 → 00100101₂ = 37
    data[1] = 7;
    data[2] = 9;
    data[3] = (byte) -5;
    System.arraycopy("TIME".getBytes("US-ASCII"), 0, data, 12, 4);
    data[44] = data[47] = 1;

    SntpMessage msg = new SntpMessage(data, -1.0, (byte) 4);
    byte[] out = msg.getBytes();
    assertEquals(37, out[0] & 0xFF);
    assertEquals(7, out[1] & 0xFF);
    assertEquals(9, out[2] & 0xFF);
    assertEquals(-5, out[3]);
    assertArrayEquals("TIME".getBytes("US-ASCII"), java.util.Arrays.copyOfRange(out, 12, 16));
  }

  @Test
  public void toUnsignedUtilityWorksViaReflection() throws Exception {
    // Privát toUnsigned: negatív byte -> unsigned short
    java.lang.reflect.Method m = SntpMessage.class
            .getDeclaredMethod("toUnsigned", byte.class);
    m.setAccessible(true);
    assertEquals((short) 127, m.invoke(null, (byte) 0x7F));
    assertEquals((short) 255, m.invoke(null, (byte) 0xFF));
  }

}
