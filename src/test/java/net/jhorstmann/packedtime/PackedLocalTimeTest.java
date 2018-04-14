package net.jhorstmann.packedtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

public class PackedLocalTimeTest {
    @Test
    public void now() {
        LocalTime now = LocalTime.now().withNano(456_000_000);
        PackedLocalTime packed = PackedLocalTime.fromLocalTime(now);

        Assertions.assertEquals(now, packed.toLocalTime());
        Assertions.assertEquals(now.toString(), packed.toString());
    }

    @Test
    public void min() {
        LocalTime min = LocalTime.of(0, 0, 0);
        PackedLocalTime packed = PackedLocalTime.fromLocalTime(min);

        Assertions.assertEquals(min, packed.toLocalTime());
        Assertions.assertEquals(min.toString(), packed.toString());
    }

    @Test
    public void max() {
        LocalTime max = LocalTime.of(23, 59, 59);
        PackedLocalTime packed = PackedLocalTime.fromLocalTime(max);

        Assertions.assertEquals(max, packed.toLocalTime());
        Assertions.assertEquals(max.toString(), packed.toString());
    }

}
