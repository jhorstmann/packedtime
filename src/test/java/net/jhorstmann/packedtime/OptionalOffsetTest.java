package net.jhorstmann.packedtime;

import org.junit.jupiter.api.Test;

import java.text.ParsePosition;
import java.time.*;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Locale;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

public class OptionalOffsetTest {

    private static final String WITH_OFFSET = "2018-04-16T23:04:00.000+02:00";
    private static final String WITHOUT_OFFSET = "2018-04-16T23:04";

    private static final DateTimeFormatter ISO_DATE_TIME_WITH_OPTIONAL_OFFSET = new DateTimeFormatterBuilder()
            .parseCaseInsensitive()
            .append(ISO_LOCAL_DATE_TIME)
            .optionalStart()
            .appendOffsetId()
            .toFormatter(Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT)
            .withChronology(IsoChronology.INSTANCE);

    @Test
    public void parseBest() {
        OffsetDateTime odt = (OffsetDateTime) ISO_DATE_TIME_WITH_OPTIONAL_OFFSET.parseBest(WITHOUT_OFFSET, OffsetDateTime::from, new TemporalQuery<OffsetDateTime>() {
            @Override
            public OffsetDateTime queryFrom(TemporalAccessor temporal) {
                return LocalDateTime.from(temporal).atOffset(ZoneOffset.UTC);
            }
        });
        System.out.println(odt);
    }

    @Test
    public void parseUnresolved() {
        TemporalAccessor ta = ISO_DATE_TIME_WITH_OPTIONAL_OFFSET.parseUnresolved(WITHOUT_OFFSET, new ParsePosition(0));
        OffsetDateTime odt = OffsetDateTime.of(ta.get(ChronoField.YEAR),
                ta.get(ChronoField.MONTH_OF_YEAR),
                ta.get(ChronoField.DAY_OF_MONTH),
                ta.get(ChronoField.HOUR_OF_DAY),
                ta.get(ChronoField.MINUTE_OF_HOUR),
                ta.isSupported(ChronoField.SECOND_OF_MINUTE) ? ta.get(ChronoField.SECOND_OF_MINUTE) : 0,
                ta.isSupported(ChronoField.NANO_OF_SECOND) ? ta.get(ChronoField.NANO_OF_SECOND) : 0,
                ZoneOffset.ofTotalSeconds(ta.isSupported(ChronoField.OFFSET_SECONDS) ? ta.get(ChronoField.OFFSET_SECONDS) : 0));
        System.out.println(odt);
    }

    @Test
    public void parsePackedWithoutOffset() {
        PackedOffsetDateTime parse = PackedOffsetDateTime.parseWithDefaultUTC(WITHOUT_OFFSET);
        System.out.println(parse);
    }

}
