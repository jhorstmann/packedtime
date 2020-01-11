# Memory-efficient wrapper for java.time values with fast toString operations

[![Build Status](https://travis-ci.org/jhorstmann/packedtime.svg?branch=master)](https://travis-ci.org/jhorstmann/packedtime)
[![Release](https://img.shields.io/github/release/jhorstmann/packedtime.svg)](https://github.com/jhorstmann/packedtime/releases)
[![Maven Central](https://img.shields.io/maven-central/v/net.jhorstmann/packedtime.svg)](https://maven-badges.herokuapp.com/maven-central/net.jhorstmann/packedtime)

The layout of `java.time` types like `OffsetDateTime` is not very space efficient.
In jdk8, the memory layout of `OffsetDateTime` looks like the following:

```
bytes | field     | type
    4 | dateTime  | LocalDateTime
    4 | .date     | LocalDate
    4 |  .year    | int
    2 |  .month   | short
    2 |  .day     | short
    4 | .time     | LocalTime
    1 |  .hour    | byte
    1 |  .minute  | byte
    1 |  .second  | byte
    1 |  (padding)|
    4 |  .nano    | int
    4 | .offset   | ZoneOffset
    4 |  .seconds | int
    4 |  .id      | String
```

Assuming 4 byte pointers and no other object overhead, this already results in a size of 40 bytes per `OffsetDateTime` object,
even more since each object has about 4-8 bytes of overhead. Most of these fields can be represented using less bits and packed
into one field, without the overhead of intermediate objects.

## Packed representation and limitations

Date, time and offset information is stored in packed into one long field.

```
field  | bits | limitations
year   |   16 | currently restricted to -9999 to 9999 range to simplify `toString` method
month  |    4 |
day    |    5 |
hour   |    5 |
minute |    6 |
second |    6 |
nano   |   10 | limited to millisecond precision
offset |   12 | limited to minute precision, same range as `ZoneOffset` (-18:00 to +18:00)
```

For `PackedZonedDateTime` the `offset` value is used as an internal identifier for the timezone region and offset.

**Warning:** This means a program can at most handle 4096 distinct region / offset values.
Be very careful when accepting region and offset from untrusted sources to avoid denial of service issues.

## Equality

In constrast to `java.time` values, `equals` and `hashCode` are based solely on the internal representation. To compare
objects in the same way as `java.time`, use an explicit `Comparator`. For example:

```java
Comparator<OffsetDateTime> comparator = Comparator.comparing(OffsetDateTime::toOffsetTime);
```

## Benchmarks

Benchmarked using JMH on an EC2 m5.large (Xeon Platinum 8175M)

```
# JMH version: 1.21
# VM version: JDK 11.0.5, OpenJDK 64-Bit Server VM, 11.0.5+10-post-Ubuntu-0ubuntu1.1

Benchmark                                                  Mode  Cnt      Score     Error  Units
PackedOffsetDateTimeBenchmark.formatOffsetDateTime        thrpt    6   3269.215 ±   8.940  ops/s
PackedOffsetDateTimeBenchmark.formatPackedOffsetDateTime  thrpt    6  18689.788 ± 693.067  ops/s
PackedOffsetDateTimeBenchmark.parseOffsetDateTime         thrpt    6    643.025 ±   2.320  ops/s
PackedOffsetDateTimeBenchmark.parsePackedOffsetDateTime   thrpt    6  15827.038 ±  26.575  ops/s
```