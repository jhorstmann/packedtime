package net.jhorstmann.packedtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

public class PackedLocalDateTimeTest {
    @Test
    public void now() {
        LocalDateTime now = LocalDateTime.now().withNano(789_000_000);
        PackedLocalDateTime packed = PackedLocalDateTime.fromLocalDateTime(now);

        Assertions.assertEquals(now, packed.toLocalDateTime());
        Assertions.assertEquals(now.toString(), packed.toString());
        Assertions.assertEquals(packed, PackedLocalDateTime.parse(now.toString()));
    }

    @Test
    public void yearZero() {
        LocalDateTime zero = LocalDateTime.of(0, 1, 1, 0, 0, 0, 0);
        PackedLocalDateTime packed = PackedLocalDateTime.fromLocalDateTime(zero);

        Assertions.assertEquals(zero, packed.toLocalDateTime());
        Assertions.assertEquals(zero.toString(), packed.toString());
    }

    @Test
    public void minYear() {
        LocalDateTime min = LocalDateTime.of(-9999, 1, 1, 0, 0, 0, 0);
        PackedLocalDateTime packed = PackedLocalDateTime.fromLocalDateTime(min);

        Assertions.assertEquals(min, packed.toLocalDateTime());
        Assertions.assertEquals(min.toString(), packed.toString());
    }

    @Test
    public void maxYear() {
        LocalDateTime max = LocalDateTime.of(9999, 12, 31, 23, 59, 59, 999_000_000);
        PackedLocalDateTime packed = PackedLocalDateTime.fromLocalDateTime(max);

        Assertions.assertEquals(max, packed.toLocalDateTime());
        Assertions.assertEquals(max.toString(), packed.toString());
    }

}
