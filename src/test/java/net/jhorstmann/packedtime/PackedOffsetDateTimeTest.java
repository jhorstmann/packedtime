package net.jhorstmann.packedtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class PackedOffsetDateTimeTest {
    @Test
    public void now() {
        OffsetDateTime now = OffsetDateTime.now();
        PackedOffsetDateTime packed = PackedOffsetDateTime.fromOffsetDateTime(now);

        Assertions.assertEquals(now, packed.toOffsetDateTime());
        Assertions.assertEquals(now.toString(), packed.toOffsetDateTime().toString());
    }

    @Test
    public void yearZero() {
        OffsetDateTime min = OffsetDateTime.of(0, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        PackedOffsetDateTime packed = PackedOffsetDateTime.fromOffsetDateTime(min);

        Assertions.assertEquals(min, packed.toOffsetDateTime());
        Assertions.assertEquals(min.toString(), packed.toOffsetDateTime().toString());
    }

    @Test
    public void minYear() {
        OffsetDateTime min = OffsetDateTime.of(-9999, 1, 1, 0, 0, 0, 0, ZoneOffset.UTC);
        PackedOffsetDateTime packed = PackedOffsetDateTime.fromOffsetDateTime(min);

        Assertions.assertEquals(min, packed.toOffsetDateTime());
        Assertions.assertEquals(min.toString(), packed.toOffsetDateTime().toString());
    }

    @Test
    public void maxYear() {
        OffsetDateTime min = OffsetDateTime.of(9999, 12, 31, 23, 59, 59, 999_000_000, ZoneOffset.UTC);
        PackedOffsetDateTime packed = PackedOffsetDateTime.fromOffsetDateTime(min);

        Assertions.assertEquals(min, packed.toOffsetDateTime());
        Assertions.assertEquals(min.toString(), packed.toOffsetDateTime().toString());
    }

    @Test
    public void minOffset() {
        OffsetDateTime min = OffsetDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(-18));
        PackedOffsetDateTime packed = PackedOffsetDateTime.fromOffsetDateTime(min);

        Assertions.assertEquals(min, packed.toOffsetDateTime());
        Assertions.assertEquals(min.toString(), packed.toOffsetDateTime().toString());
    }

    @Test
    public void maxOffset() {
        OffsetDateTime min = OffsetDateTime.of(2018, 1, 1, 0, 0, 0, 0, ZoneOffset.ofHours(18));
        PackedOffsetDateTime packed = PackedOffsetDateTime.fromOffsetDateTime(min);

        Assertions.assertEquals(min, packed.toOffsetDateTime());
        Assertions.assertEquals(min.toString(), packed.toOffsetDateTime().toString());
    }

}
