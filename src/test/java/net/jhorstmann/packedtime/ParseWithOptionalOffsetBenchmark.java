package net.jhorstmann.packedtime;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.text.ParsePosition;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.chrono.IsoChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.time.temporal.TemporalQuery;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

import static java.time.format.DateTimeFormatter.ISO_LOCAL_DATE_TIME;

@BenchmarkMode(value = Mode.Throughput)
public class ParseWithOptionalOffsetBenchmark {

    @State(Scope.Benchmark)
    public static class Input {
        final int validPercentage = 25;
        final int size = 1000;
        final String[] dates;

        public Input() {
            this.dates = new String[size];
            ThreadLocalRandom r = ThreadLocalRandom.current();
            for (int i = 0; i < size; i++) {
                OffsetDateTime odt = OffsetDateTime.of(1900 + r.nextInt(200), r.nextInt(12) + 1, r.nextInt(28) + 1, r.nextInt(24), r.nextInt(60), r.nextInt(60), r.nextInt(1000) * 1000 * 1000, ZoneOffset.ofTotalSeconds((r.nextInt(36 * 60) - 18 * 60) * 60));

                if (r.nextInt(100) < validPercentage) {
                    this.dates[i] = odt.toString();
                } else {
                    this.dates[i] = odt.toLocalDateTime().toString();
                }
            }
        }
    }

    @Benchmark
    public void parseTryCatch(Input input, Blackhole blackhole) {
        for (int i = 0; i < input.size; i++) {
            String date = input.dates[i];
            OffsetDateTime odt;
            try {
                odt = OffsetDateTime.parse(date);
            } catch (DateTimeParseException ex) {
                odt = LocalDateTime.parse(date).atOffset(ZoneOffset.UTC);
            }
            blackhole.consume(odt);
        }
    }

    private static final DateTimeFormatter ISO_DATE_TIME_WITH_OPTIONAL_OFFSET = new DateTimeFormatterBuilder()
            .append(ISO_LOCAL_DATE_TIME)
            .optionalStart()
            .appendOffsetId()
            .toFormatter(Locale.ROOT)
            .withResolverStyle(ResolverStyle.STRICT)
            .withChronology(IsoChronology.INSTANCE);

    @Benchmark
    public void parseBest(Input input, Blackhole blackhole) {
        TemporalQuery<OffsetDateTime> fromLocalAtUTC = temporal -> LocalDateTime.from(temporal).atOffset(ZoneOffset.UTC);
        for (int i = 0; i < input.size; i++) {
            String date = input.dates[i];
            OffsetDateTime odt = (OffsetDateTime) ISO_DATE_TIME_WITH_OPTIONAL_OFFSET.parseBest(date, OffsetDateTime::from, fromLocalAtUTC);
            blackhole.consume(odt);
        }
    }


    private static OffsetDateTime parseUnresolved(String date) {
        TemporalAccessor ta = ISO_DATE_TIME_WITH_OPTIONAL_OFFSET.parseUnresolved(date, new ParsePosition(0));

        return OffsetDateTime.of(ta.get(ChronoField.YEAR),
                ta.get(ChronoField.MONTH_OF_YEAR),
                ta.get(ChronoField.DAY_OF_MONTH),
                ta.get(ChronoField.HOUR_OF_DAY),
                ta.get(ChronoField.MINUTE_OF_HOUR),
                ta.get(ChronoField.SECOND_OF_MINUTE),
                ta.isSupported(ChronoField.NANO_OF_SECOND) ? ta.get(ChronoField.NANO_OF_SECOND) : 0,
                ZoneOffset.ofTotalSeconds(ta.isSupported(ChronoField.OFFSET_SECONDS) ? ta.get(ChronoField.OFFSET_SECONDS) : 0));
    }

    @Benchmark
    public void parseUnresolved(Input input, Blackhole blackhole) {
        for (int i = 0; i < input.size; i++) {
            OffsetDateTime odt = parseUnresolved(input.dates[i]);

            blackhole.consume(odt);
        }
    }

    @Benchmark
    public void parsePacked(Input input, Blackhole blackhole) {
        for (int i = 0; i < input.size; i++) {
            OffsetDateTime odt = PackedOffsetDateTime.parseWithDefaultUTC(input.dates[i]).toOffsetDateTime();

            blackhole.consume(odt);
        }
    }

    public static void main(String[] args) throws RunnerException {

        Options options = new OptionsBuilder()
                .include(ParseWithOptionalOffsetBenchmark.class.getName())
                .forks(1)
                .threads(1)
                .warmupIterations(8)
                .measurementIterations(6)
                .build();

        new Runner(options).run();
    }
}
