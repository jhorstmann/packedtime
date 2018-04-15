package net.jhorstmann.packedtime;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.concurrent.ThreadLocalRandom;

@BenchmarkMode(value = Mode.Throughput)
public class PackedOffsetDateTimeBenchmark {


    @State(Scope.Benchmark)
    public static class Input {
        final int size = 1000;
        final OffsetDateTime[] base;
        final long[] packed;
        final String[] formatted;

        public Input() {
            this.base = new OffsetDateTime[size];
            this.packed = new long[size];
            this.formatted = new String[size];
            ThreadLocalRandom r = ThreadLocalRandom.current();
            for (int i = 0; i < size; i++) {
                OffsetDateTime odt = OffsetDateTime.of(r.nextInt(3000) - 1000, r.nextInt(12) + 1, r.nextInt(28) + 1, r.nextInt(24), r.nextInt(60), r.nextInt(60), r.nextInt(1000) * 1000 * 1000, ZoneOffset.ofTotalSeconds((r.nextInt(36 * 60) - 18 * 60) * 60));
                PackedOffsetDateTime packed = PackedOffsetDateTime.fromOffsetDateTime(odt);

                String str = odt.toString();

                if (!(odt.equals(packed.toOffsetDateTime()))) {
                    throw new AssertionError("dates not equal " + odt + " != " + packed);
                }
                if (!(str.equals(packed.toString()))) {
                    throw new AssertionError("strings not equal " + odt + " != " + packed);
                }
                PackedOffsetDateTime parsed = DateTimeParser.parseOffsetDateTime(str);
                if (!parsed.toOffsetDateTime().equals(odt)) {
                    throw new AssertionError("parsed dates not equal " + odt + " != " + parsed);
                }

                this.base[i] = odt;
                this.packed[i] = packed.getValue();
                this.formatted[i] = str;
            }

        }
    }

    @Benchmark
    public void formatOffsetDateTime(Input input, Blackhole blackhole) {
        for (int i = 0; i < input.size; i++) {
            blackhole.consume(input.base[i].toString());
        }
    }

    @Benchmark
    public void formatPackedOffsetDateTime(Input input, Blackhole blackhole) {
        for (int i = 0; i < input.size; i++) {
            blackhole.consume(PackedOffsetDateTime.valueOf(input.packed[i]).toString());
        }
    }

    @Benchmark
    public void parseOffsetDateTime(Input input, Blackhole blackhole) {
        for (int i = 0; i < input.size; i++) {
            blackhole.consume(OffsetDateTime.parse(input.formatted[i]));
        }
    }

    @Benchmark
    public void parsePackedOffsetDateTime(Input input, Blackhole blackhole) {
        for (int i = 0; i < input.size; i++) {
            blackhole.consume(PackedOffsetDateTime.parse(input.formatted[i]));
        }
    }

    public static void main(String[] args) throws RunnerException {

        Options options = new OptionsBuilder()
                .include(PackedOffsetDateTimeBenchmark.class.getName())
                .forks(1)
                .threads(1)
                .warmupIterations(8)
                .measurementIterations(6)
                .build();

        new Runner(options).run();
    }
}
