package net.jhorstmann.packedtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

public class DateTimeParserTest {
    @Test
    public void test() {
        String str = "1216-02-28T23:09:05.864-15:55";
        PackedOffsetDateTime parsed = DateTimeParser.parseOffsetDateTime(str);

        Assertions.assertEquals(OffsetDateTime.parse(str), parsed.toOffsetDateTime());
    }

}
