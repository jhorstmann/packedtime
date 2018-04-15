package net.jhorstmann.packedtime;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class DateTimeParserRandomTest {
    @ParameterizedTest
    @MethodSource("input")
    public void testOffsetDateTime(OffsetDateTime odt) {

        String str = odt.toString();
        PackedOffsetDateTime packed = PackedOffsetDateTime.parse(str);

        Assertions.assertEquals(odt, packed.toOffsetDateTime());
        Assertions.assertEquals(str, packed.toString());
    }

    @ParameterizedTest
    @MethodSource("input")
    public void testLocalDateTime(OffsetDateTime odt) {

        LocalDateTime ldt = odt.toLocalDateTime();
        String str = ldt.toString();
        PackedLocalDateTime packed = PackedLocalDateTime.parse(str);

        Assertions.assertEquals(ldt, packed.toLocalDateTime());
        Assertions.assertEquals(str, packed.toString());
    }

    @ParameterizedTest
    @MethodSource("input")
    public void testLocalDate(OffsetDateTime odt) {

        LocalDate ld = odt.toLocalDate();
        String str = ld.toString();
        PackedLocalDate packed = PackedLocalDate.parse(str);

        Assertions.assertEquals(ld, packed.toLocalDate());
        Assertions.assertEquals(str, packed.toString());
    }

    @ParameterizedTest
    @MethodSource("input")
    public void testOffsetTime(OffsetDateTime odt) {

        OffsetTime ot = odt.toOffsetTime();
        String str = ot.toString();
        PackedOffsetTime packed = PackedOffsetTime.parse(str);

        Assertions.assertEquals(ot, packed.toOffsetTime());
        Assertions.assertEquals(str, packed.toString());
    }

    @ParameterizedTest
    @MethodSource("input")
    public void testLocalTime(OffsetDateTime odt) {

        LocalTime lt = odt.toLocalTime();
        String str = lt.toString();
        PackedLocalTime packed = PackedLocalTime.parse(str);

        Assertions.assertEquals(lt, packed.toLocalTime());
        Assertions.assertEquals(str, packed.toString());
    }

    private static final List<String> ZONES = new ArrayList<>(ZoneId.getAvailableZoneIds());

    @ParameterizedTest
    @MethodSource("input")
    public void testZonedDateTime(OffsetDateTime odt) {

        ZonedDateTime zdt = odt.atZoneSimilarLocal(ZoneId.of(ZONES.get(ThreadLocalRandom.current().nextInt(ZONES.size()))));
        //ZonedDateTime zdt = odt.atZoneSameInstant(ZoneId.of("Europe/Berlin"));

        String str = zdt.toString();
        PackedZonedDateTime packed = PackedZonedDateTime.parse(str);

        Assertions.assertEquals(zdt, packed.toZonedDateTime());
        Assertions.assertEquals(str, packed.toString());
    }

    private static Stream<OffsetDateTime> input() {
        ThreadLocalRandom r = ThreadLocalRandom.current();

        return IntStream.range(0, 100)
                .mapToObj(i -> {
                    int year = r.nextInt(3000) - 1000;
                    int month = r.nextInt(12) + 1;
                    int day = r.nextInt(Month.of(month).length(Year.isLeap(year))) + 1;
                    int hour = r.nextInt(24);
                    int minute = r.nextInt(60);
                    int second = r.nextInt(60);
                    int nano = r.nextInt(1000) * 1_000_000;
                    int offsetSeconds = (r.nextInt(36 * 60) - 18 * 60) * 60;
                    return OffsetDateTime.of(year, month, day,
                            hour, minute, second, nano,
                            ZoneOffset.ofTotalSeconds(offsetSeconds));
                });
    }

}
