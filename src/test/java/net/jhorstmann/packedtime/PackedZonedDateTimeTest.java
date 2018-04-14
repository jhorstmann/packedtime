package net.jhorstmann.packedtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class PackedZonedDateTimeTest {

    @Test
    public void testUTC() {
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 14, 21, 25, 27, 0);

        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.of("Z"));
        PackedZonedDateTime packed = PackedZonedDateTime.fromZonedDateTime(zdt);

        Assertions.assertEquals(zdt, packed.toZonedDateTime());
        Assertions.assertEquals(zdt.toString(), packed.toString());
    }

    @Test
    public void testFixedOffset() {
        LocalDateTime localDateTime = LocalDateTime.of(2018, 4, 14, 21, 25, 27, 0);

        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.of("+02:00"));
        PackedZonedDateTime packed = PackedZonedDateTime.fromZonedDateTime(zdt);

        Assertions.assertEquals(zdt, packed.toZonedDateTime());
        Assertions.assertEquals(zdt.toString(), packed.toString());
    }

    @Test
    public void testSummerTime() {
        LocalDateTime localDateTime = LocalDateTime.of(2017, 10, 29, 1, 30, 0, 0);

        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.of("Europe/Berlin"));
        PackedZonedDateTime packed = PackedZonedDateTime.fromZonedDateTime(zdt);

        Assertions.assertEquals(zdt, packed.toZonedDateTime());
        Assertions.assertEquals(zdt.toString(), packed.toString());
    }

    @Test
    public void testWinterTime() {
        LocalDateTime localDateTime = LocalDateTime.of(2017, 10, 29, 5, 30, 0, 0);

        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.of("Europe/Berlin"));
        PackedZonedDateTime packed = PackedZonedDateTime.fromZonedDateTime(zdt);

        Assertions.assertEquals(zdt, packed.toZonedDateTime());
        Assertions.assertEquals(zdt.toString(), packed.toString());
    }

    @Test
    public void testTransition() {
        LocalDateTime localDateTime = LocalDateTime.of(2017, 10, 29, 2, 30, 0, 0);

        ZonedDateTime zdt = ZonedDateTime.of(localDateTime, ZoneId.of("Europe/Berlin"));
        PackedZonedDateTime packed = PackedZonedDateTime.fromZonedDateTime(zdt);

        Assertions.assertEquals(zdt, packed.toZonedDateTime());
        Assertions.assertEquals(zdt.toString(), packed.toString());
    }

    @Test
    public void testTransitionSummer() {
        LocalDateTime localDateTime = LocalDateTime.of(2017, 10, 29, 2, 30, 0, 0);

        ZonedDateTime zdt = ZonedDateTime.ofLocal(localDateTime, ZoneId.of("Europe/Berlin"), ZoneOffset.ofHours(2));
        PackedZonedDateTime packed = PackedZonedDateTime.fromZonedDateTime(zdt);

        Assertions.assertEquals(zdt, packed.toZonedDateTime());
        Assertions.assertEquals(zdt.toString(), packed.toString());
    }

    @Test
    public void testTransitionWinter() {
        LocalDateTime localDateTime = LocalDateTime.of(2017, 10, 29, 2, 30, 0, 0);

        ZonedDateTime zdt = ZonedDateTime.ofLocal(localDateTime, ZoneId.of("Europe/Berlin"), ZoneOffset.ofHours(1));
        PackedZonedDateTime packed = PackedZonedDateTime.fromZonedDateTime(zdt);

        Assertions.assertEquals(zdt, packed.toZonedDateTime());
        Assertions.assertEquals(zdt.toString(), packed.toString());
    }

}
