package net.jhorstmann.packedtime;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PackedZonedDateTime extends AbstractPackedDateTime {

    static class ZoneAndOffset {
        final ZoneId id;
        final ZoneOffset offset;

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
        private int counter = 0;
        private final Map<ZoneAndOffset, Integer> ids = new HashMap<>();
        private final ZoneAndOffset[] zones = new ZoneAndOffset[(1 << AbstractPackedDateTime.OFFSET_BITS)-1];

        synchronized int getId(ZonedDateTime zonedDateTime) {
            ZoneAndOffset zoneAndOffset = toZoneAndOffset(zonedDateTime);
            return ids.computeIfAbsent(zoneAndOffset, k -> {
                int i = counter++;
                if (i >= zones.length) {
                    throw new IllegalStateException("ZoneAndOffsetCache overflow");
                }
                zones[i] = zoneAndOffset;
                return i;
            });
        }

        synchronized ZoneAndOffset getZoneId(int id) {
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
            return ZonedDateTime.of(extractYear(), extractMonth(), extractDay(),
                    extractHour(), extractMinute(), extractSecond(), extractNano(),
                    zoneAndOffset.id);
        } else {
            LocalDateTime localDateTime = LocalDateTime.of(extractYear(), extractMonth(), extractDay(),
                    extractHour(), extractMinute(), extractSecond(), extractNano());
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
        char[] buf = new char[36];
        int i = 0;

        i = appendDate(buf, i);

        buf[i++] = 'T';

        i = appendTime(buf, i);

        ZoneAndOffset zoneAndOffset = CACHE.getZoneId(extractOffsetId());

        ZoneOffset offset = zoneAndOffset.offset;
        ZoneId zoneId = zoneAndOffset.id;

        int totalSeconds = offset.getTotalSeconds();

        if (totalSeconds == 0) {
            buf[i++] = 'Z';
        } else {
            int offsetMinute = totalSeconds / 60;
            int offsetSecond = Math.abs(totalSeconds) % 60;

            i = appendOffsetMinute(offsetMinute, buf, i);

            if (offsetSecond != 0) {
                buf[i++] = ':';
                buf[i++] = (char) ('0' + offsetSecond / 10);
                buf[i++] = (char) ('0' + offsetSecond % 10);
            }
        }

        String result = new String(buf, 0, i);

        if (zoneId != offset) {
            result += '[' + zoneId.toString() + ']';
        }

        return result;
    }


}
