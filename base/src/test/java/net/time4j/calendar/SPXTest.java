package net.time4j.calendar;

import org.junit.Test;

import java.io.*;

import static org.junit.Assert.*;

public class SPXTest {

    @Test
    public void testPersianSerializationRoundTrip() throws Exception {
        PersianCalendar orig = PersianCalendar.of(1444, 9, 1);

        SPX spxOut = new SPX(orig, SPX.PERSIAN);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bout);
        spxOut.writeExternal(oos);
        oos.flush();

        SPX spxIn = new SPX();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
        spxIn.readExternal(ois);

        Object resolved = spxIn.readResolve();
        assertTrue(resolved instanceof PersianCalendar);
        PersianCalendar readBack = (PersianCalendar) resolved;
        assertEquals(orig.getYear(),       readBack.getYear());
        assertEquals(orig.getMonth().getValue(), readBack.getMonth().getValue());
        assertEquals(orig.getDayOfMonth(), readBack.getDayOfMonth());
    }

    @Test
    public void testJulianSerializationRoundTrip() throws Exception {
        JulianCalendar orig = JulianCalendar.of(net.time4j.history.HistoricEra.BC, 44, 3, 15);

        SPX spxOut = new SPX(orig, SPX.JULIAN);

        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bout);
        spxOut.writeExternal(oos);
        oos.flush();

        SPX spxIn = new SPX();
        ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(bout.toByteArray()));
        spxIn.readExternal(ois);

        Object resolved = spxIn.readResolve();
        assertTrue(resolved instanceof JulianCalendar);
        JulianCalendar readBack = (JulianCalendar) resolved;
        assertEquals(orig.getProlepticYear(), readBack.getProlepticYear());
        assertEquals(orig.getMonth().getValue(), readBack.getMonth().getValue());
        assertEquals(orig.getDayOfMonth(), readBack.getDayOfMonth());
    }

    @Test(expected = InvalidClassException.class)
    public void testWriteExternalWithUnsupportedType() throws Exception {
        SPX spx = new SPX("foo", 99);
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ObjectOutputStream oos = new ObjectOutputStream(bout);
        spx.writeExternal(oos);
    }
}
