package net.jhorstmann.packedtime;

import java.time.*;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class PackedZonedDateTime extends AbstractPackedDateTime {

    static class ZoneAndOffset {
        private final ZoneId id;
        private final ZoneOffset offset;

        ZoneAndOffset(ZoneId id, ZoneOffset offset) {
            this.id = id;
            this.offset = offset;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            ZoneAndOffset that = (ZoneAndOffset) o;
            return Objects.equals(id, that.id) &&
                    Objects.equals(offset, that.offset);
        }

        @Override
        public int hashCode() {
            return Objects.hash(id, offset);
        }
    }

    static class ZoneAndOffsetCache {
        private final AtomicInteger counter = new AtomicInteger();
        private final Map<ZoneAndOffset, Integer> ids = new ConcurrentHashMap<>();
        private final ZoneAndOffset[] zones = new ZoneAndOffset[(1 << AbstractPackedDateTime.OFFSET_BITS)-1];

        int getId(ZonedDateTime zonedDateTime) {
            ZoneAndOffset zoneAndOffset = toZoneAndOffset(zonedDateTime);
            return ids.computeIfAbsent(zoneAndOffset, k -> {
                int i = counter.getAndIncrement();
                zones[i] = zoneAndOffset;
                return i;
            });
        }

        ZoneAndOffset getZoneId(int id) {
            return zones[id];
        }

        private static ZoneAndOffset toZoneAndOffset(ZonedDateTime zonedDateTime) {
            ZoneId zoneId = zonedDateTime.getZone();
            ZoneOffset zoneOffset = zonedDateTime.getOffset();
            return new ZoneAndOffset(zoneId, zoneOffset);
        }
    }

    private static final ZoneAndOffsetCache CACHE = new ZoneAndOffsetCache();

    private PackedZonedDateTime(long value) {
        super(value);
    }

    public static PackedZonedDateTime valueOf(long value) {
        return new PackedZonedDateTime(value);
    }

    public static PackedZonedDateTime fromZonedDateTime(ZonedDateTime zonedDateTime) {
        int id = CACHE.getId(zonedDateTime);
        return new PackedZonedDateTime(encode(zonedDateTime.getYear(),
                zonedDateTime.getMonthValue(),
                zonedDateTime.getDayOfMonth(),
                zonedDateTime.getHour(),
                zonedDateTime.getMinute(),
                zonedDateTime.getSecond(),
                zonedDateTime.getNano(),
                id));
    }

    public static PackedZonedDateTime parse(String str) {
        return fromZonedDateTime(ZonedDateTime.parse(str));
    }

    public static ZonedDateTime toZonedDateTime(long value) {
        return valueOf(value).toZonedDateTime();
    }

    public ZonedDateTime toZonedDateTime() {
        ZoneAndOffset zoneAndOffset = CACHE.getZoneId(extractOffsetId());
        if (zoneAndOffset.id == zoneAndOffset.offset) {
            // no preferred offset
            return ZonedDateTime.of(getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond(), getNano(), zoneAndOffset.id);
        } else {
            LocalDateTime localDateTime = LocalDateTime.of(getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond(), getNano());
            return ZonedDateTime.ofLocal(localDateTime, zoneAndOffset.id, zoneAndOffset.offset);
        }
    }

    public ZoneId getZone() {
        ZoneAndOffset zoneAndOffset = CACHE.getZoneId(extractOffsetId());
        return zoneAndOffset.id;
    }

    public ZoneOffset getOffset() {
        ZoneAndOffset zoneAndOffset = CACHE.getZoneId(extractOffsetId());
        return zoneAndOffset.offset;
    }

    public int getYear() {
        return extractYear();
    }

    public int getMonth() {
        return extractMonth();
    }

    public int getDay() {
        return extractDay();
    }

    public int getHour() {
        return extractHour();
    }

    public int getMinute() {
        return extractMinute();
    }

    public int getSecond() {
        return extractSecond();
    }

    public int getMilliSecond() {
        return extractMilli();
    }

    public int getNano() {
        return extractMilli() * 1_000_000;
    }

    public String toString() {
        char[] buf = new char[32];
        int i = 0;

        i = appendDate(buf, i);

        buf[i++] = 'T';

        i = appendTime(buf, i);

        ZoneAndOffset zoneAndOffset = CACHE.getZoneId(extractOffsetId());

        ZoneOffset offset = zoneAndOffset.offset;
        ZoneId zoneId = zoneAndOffset.id;

        i = appendOffsetMinute(offset.getTotalSeconds() / 60, buf, i);

        String result = new String(buf, 0, i);

        if (zoneId != offset) {
            result += '[' + zoneId.toString() + ']';
        }

        return result;
    }


}
