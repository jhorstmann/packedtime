package net.jhorstmann.packedtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.zone.ZoneRulesProvider;
import java.util.Set;

public class PackedZonedDateTimeRegionTest {
    public static Set<String> availableZoneIds() {
        return ZoneRulesProvider.getAvailableZoneIds();
    }

    @ParameterizedTest
    @MethodSource("availableZoneIds")
    public void testRegion(String region) {
        ZonedDateTime zdt = ZonedDateTime.of(2018, 4, 14, 20, 51, 30, 123_000_000, ZoneId.of(region));

        PackedZonedDateTime packed = PackedZonedDateTime.fromZonedDateTime(zdt);

        Assertions.assertEquals(zdt, packed.toZonedDateTime());
        Assertions.assertEquals(zdt.toString(), packed.toString());
    }
}
