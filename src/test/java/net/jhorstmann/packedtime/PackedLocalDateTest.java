package net.jhorstmann.packedtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class PackedLocalDateTest {
    @Test
    public void now() {
        LocalDate now = LocalDate.now();
        PackedLocalDate packed = PackedLocalDate.fromLocalDate(now);

        Assertions.assertEquals(now, packed.toLocalDate());
        Assertions.assertEquals(now.toString(), packed.toString());
    }

    @Test
    public void yearZero() {
        LocalDate zero = LocalDate.of(0, 1, 1);
        PackedLocalDate packed = PackedLocalDate.fromLocalDate(zero);

        Assertions.assertEquals(zero, packed.toLocalDate());
        Assertions.assertEquals(zero.toString(), packed.toString());
    }

    @Test
    public void minYear() {
        LocalDate min = LocalDate.of(-9999, 1, 1);
        PackedLocalDate packed = PackedLocalDate.fromLocalDate(min);

        Assertions.assertEquals(min, packed.toLocalDate());
        Assertions.assertEquals(min.toString(), packed.toString());
    }

    @Test
    public void maxYear() {
        LocalDate max = LocalDate.of(9999, 12, 31);
        PackedLocalDate packed = PackedLocalDate.fromLocalDate(max);

        Assertions.assertEquals(max, packed.toLocalDate());
        Assertions.assertEquals(max.toString(), packed.toString());
    }

}
