package net.jhorstmann.packedtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class PackedOffsetDateTimeTest {
    @Test
    public void now() {
        OffsetDateTime now = OffsetDateTime.now().withNano(123_000_000);
        PackedOffsetDateTime packed = PackedOffsetDateTime.fromOffsetDateTime(now);

        Assertions.assertEquals(now, packed.toOffsetDateTime());
        Assertions.assertEquals(now.toString(), packed.toString());
    }

    @Test
    public void yearZero() {
        OffsetDateTime zero = OffsetDateTime.of(0, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        PackedOffsetDateTime packed = PackedOffsetDateTime.fromOffsetDateTime(zero);

        Assertions.assertEquals(zero, packed.toOffsetDateTime());
        Assertions.assertEquals(zero.toString(), packed.toString());
    }

    @Test
    public void minYear() {
        OffsetDateTime min = OffsetDateTime.of(-9999, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        PackedOffsetDateTime packed = PackedOffsetDateTime.fromOffsetDateTime(min);

        Assertions.assertEquals(min, packed.toOffsetDateTime());
        Assertions.assertEquals(min.toString(), packed.toString());
    }

    @Test
    public void maxYear() {
        OffsetDateTime max = OffsetDateTime.of(9999, 12, 31, 23, 59, 59, 999_000_000, ZoneOffset.UTC);
        PackedOffsetDateTime packed = PackedOffsetDateTime.fromOffsetDateTime(max);

        Assertions.assertEquals(max, packed.toOffsetDateTime());
        Assertions.assertEquals(max.toString(), packed.toString());
    }

    @Test
    public void negativeOffset() {
        OffsetDateTime min = OffsetDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(-1));
        PackedOffsetDateTime packed = PackedOffsetDateTime.fromOffsetDateTime(min);

        Assertions.assertEquals(min, packed.toOffsetDateTime());
        Assertions.assertEquals(min.toString(), packed.toString());
    }

    @Test
    public void minOffset() {
        OffsetDateTime min = OffsetDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(-18));
        PackedOffsetDateTime packed = PackedOffsetDateTime.fromOffsetDateTime(min);

        Assertions.assertEquals(min, packed.toOffsetDateTime());
        Assertions.assertEquals(min.toString(), packed.toString());
    }

    @Test
    public void maxOffset() {
        OffsetDateTime min = OffsetDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(18));
        PackedOffsetDateTime packed = PackedOffsetDateTime.fromOffsetDateTime(min);

        Assertions.assertEquals(min, packed.toOffsetDateTime());
        Assertions.assertEquals(min.toString(), packed.toString());
    }

    @Test
    public void epochMillis() {
        OffsetDateTime odt = OffsetDateTime.of(2020, 6, 1, 12, 30, 23, 0, ZoneOffset.ofHours(-2));
        PackedOffsetDateTime packed = PackedOffsetDateTime.fromOffsetDateTime(odt);

        Assertions.assertEquals(odt.toInstant().toEpochMilli(), packed.toEpochMillis());
        Assertions.assertEquals(odt.toInstant().toEpochMilli(), packed.toEpochMillis());
    }

}
