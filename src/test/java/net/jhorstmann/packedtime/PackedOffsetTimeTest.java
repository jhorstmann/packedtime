package net.jhorstmann.packedtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.time.OffsetTime;
import java.time.ZoneOffset;

public class PackedOffsetTimeTest {
    @Test
    public void now() {
        OffsetTime now = OffsetTime.now().withNano(456_000_000);
        PackedOffsetTime packed = PackedOffsetTime.fromOffsetTime(now);

        Assertions.assertEquals(now, packed.toOffsetTime());
        Assertions.assertEquals(now.toString(), packed.toString());
    }

    @Test
    public void min() {
        OffsetTime min = OffsetTime.of(0, 0, 0, 0, ZoneOffset.MIN);
        PackedOffsetTime packed = PackedOffsetTime.fromOffsetTime(min);

        Assertions.assertEquals(min, packed.toOffsetTime());
        Assertions.assertEquals(min.toString(), packed.toString());
    }

    @Test
    public void max() {
        OffsetTime max = OffsetTime.of(23, 59, 59, 999_000_000, ZoneOffset.MAX);
        PackedOffsetTime packed = PackedOffsetTime.fromOffsetTime(max);

        Assertions.assertEquals(max, packed.toOffsetTime());
        Assertions.assertEquals(max.toString(), packed.toString());
    }

}
