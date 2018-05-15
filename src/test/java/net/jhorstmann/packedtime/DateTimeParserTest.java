package net.jhorstmann.packedtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class DateTimeParserTest {
    @Test
    public void shouldParseOptionalMilliseconds() {
        String str = "2018-04-26T21:31:42+02:00";
        PackedOffsetDateTime parsed = DateTimeParser.parseOffsetDateTime(str);

        Assertions.assertEquals(0, parsed.getMilliSecond());
        Assertions.assertEquals(OffsetDateTime.parse(str), parsed.toOffsetDateTime());
    }

    @Test
    public void shouldParseOneDigitFraction() {
        String str = "2018-04-26T21:31:42.1+02:00";
        PackedOffsetDateTime parsed = DateTimeParser.parseOffsetDateTime(str);

        Assertions.assertEquals(100, parsed.getMilliSecond());
        Assertions.assertEquals(OffsetDateTime.parse(str), parsed.toOffsetDateTime());
    }

    @Test
    public void shouldParseTwoDigitFraction() {
        String str = "2018-04-26T21:31:42.12+02:00";
        PackedOffsetDateTime parsed = DateTimeParser.parseOffsetDateTime(str);

        Assertions.assertEquals(120, parsed.getMilliSecond());
        Assertions.assertEquals(OffsetDateTime.parse(str), parsed.toOffsetDateTime());
    }

    @Test
    public void shouldParseThreeDigitFraction() {
        String str = "2018-04-26T21:31:42.123+02:00";
        PackedOffsetDateTime parsed = DateTimeParser.parseOffsetDateTime(str);

        Assertions.assertEquals(123, parsed.getMilliSecond());
        Assertions.assertEquals(OffsetDateTime.parse(str), parsed.toOffsetDateTime());
    }

    @Test
    public void shouldParseAndIgnoreMoreThanThreeDigitFractions() {
        String str = "2018-04-26T21:31:42.123456789+02:00";
        PackedOffsetDateTime parsed = DateTimeParser.parseOffsetDateTime(str);

        Assertions.assertEquals(123, parsed.getMilliSecond());
        Assertions.assertEquals(OffsetDateTime.parse(str).withNano(123_000_000), parsed.toOffsetDateTime());
    }

    @Test
    public void shouldParseWithDefaultOffsetSeconds() {
        String str = "2018-04-26T21:31:42.123";
        PackedOffsetDateTime parsed = DateTimeParser.parseOffsetDateTimeWithDefaultOffset(str, 2*60*60);

        Assertions.assertEquals(2*60*60, parsed.getOffsetSecond());
        Assertions.assertEquals(LocalDateTime.parse(str).atOffset(ZoneOffset.of("+02:00")), parsed.toOffsetDateTime());
    }

    @Test
    public void shouldParseWithDefaultZone() {
        String str = "2018-04-26T21:31:42.123";
        PackedOffsetDateTime parsed = DateTimeParser.parseOffsetDateTimeWithDefaultZone(str, ZoneId.of("Europe/Berlin"));

        Assertions.assertEquals(2*60*60, parsed.getOffsetSecond());
        Assertions.assertEquals(LocalDateTime.parse(str).atOffset(ZoneOffset.of("+02:00")), parsed.toOffsetDateTime());
    }

    @Test
    public void shouldParseWithDefaultZoneAndRespectDST() {
        String str = "2018-01-02T21:31:42.123";
        PackedOffsetDateTime parsed = DateTimeParser.parseOffsetDateTimeWithDefaultZone(str, ZoneId.of("Europe/Berlin"));

        Assertions.assertEquals(1*60*60, parsed.getOffsetSecond());
        Assertions.assertEquals(LocalDateTime.parse(str).atZone(ZoneId.of("Europe/Berlin")).toOffsetDateTime(), parsed.toOffsetDateTime());
    }

}