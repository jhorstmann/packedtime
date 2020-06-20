package net.jhorstmann.packedtime;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;

@BenchmarkMode(value = Mode.Throughput)
public class EpochMillisBenchmark {
    @State(Scope.Benchmark)
    public static class Input {
        OffsetDateTime time;
        PackedOffsetDateTime packed;

        @Setup
        public void setup() {
            time = OffsetDateTime.parse("2020-01-19T22:15:30+01:00");
            packed = PackedOffsetDateTime.fromOffsetDateTime(time);

            long expected = time.toInstant().toEpochMilli();
            long actual = packed.toEpochMillis();

            if (expected != actual) {
                throw new AssertionError(actual + " != " + expected);
            }

        }
    }

    @Benchmark
    public long standardToInstant(Input input) {
        return input.time.toInstant().toEpochMilli();
    }

    @Benchmark
    public long standardToSecondsPlusMillis(Input input) {
        OffsetDateTime time = input.time;

        return time.toEpochSecond() * 1000 + time.get(ChronoField.MILLI_OF_SECOND);
    }

    @Benchmark
    public long packedToMillis(Input input) {
        return input.packed.toEpochMillis();
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(EpochMillisBenchmark.class.getName())
                .forks(1)
                .threads(1)
                .warmupIterations(4)
                .measurementIterations(4)
                .build();

        new Runner(options).run();
    }
}

