package net.jhorstmann.packedtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

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

}
