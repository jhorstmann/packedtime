package net.jhorstmann.packedtime;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class PackedOffsetDateTime extends AbstractPackedDateTime {
    private static final int MIN_OFFSET_MINUTES_INTERNAL = -(1 << (OFFSET_BITS - 1));
    private static final int MAX_OFFSET_MINUTES_INTERNAL = (1 << (OFFSET_BITS - 1)) - 1;

    private static final int MAX_OFFSET_HOURS = 18;
    private static final int MIN_OFFSET_HOURS = -18;
    private static final int MIN_OFFSET_MINUTES = MIN_OFFSET_HOURS * 60;
    private static final int MAX_OFFSET_MINUTES = MAX_OFFSET_HOURS * 60;

    static {
        if (!(MIN_OFFSET_MINUTES_INTERNAL < MIN_OFFSET_MINUTES || MAX_OFFSET_MINUTES_INTERNAL > MAX_OFFSET_MINUTES)) {
            throw new AssertionError("Insufficient bits to store offset range");
        }
    }

    private PackedOffsetDateTime(long value) {
        super(value);
    }

    public static PackedOffsetDateTime valueOf(long value) {
        return new PackedOffsetDateTime(value);
    }

    public static PackedOffsetDateTime fromOffsetDateTime(OffsetDateTime offsetDateTime) {
        int offsetSeconds = offsetDateTime.getOffset().getTotalSeconds();
        if (offsetSeconds % 60 != 0) {
            throw new IllegalStateException("Time zone offset with second precision is not supported");
        }

        int offsetMinutes = offsetSeconds / 60;

        if (offsetMinutes < MIN_OFFSET_MINUTES || offsetMinutes > MAX_OFFSET_MINUTES) {
            throw new IllegalStateException("Zone offset outside of allowed range " + MIN_OFFSET_HOURS + " to " + MAX_OFFSET_HOURS);
        }

        return new PackedOffsetDateTime(encode(offsetDateTime.getYear(),
                offsetDateTime.getMonthValue(),
                offsetDateTime.getDayOfMonth(),
                offsetDateTime.getHour(),
                offsetDateTime.getMinute(),
                offsetDateTime.getSecond(),
                offsetDateTime.getNano(),
                offsetMinutes + 18 * 60));
    }

    public static PackedOffsetDateTime parse(String str) {
        return fromOffsetDateTime(OffsetDateTime.parse(str));
    }

    public static OffsetDateTime toOffsetDateTime(long value) {
        return valueOf(value).toOffsetDateTime();
    }

    public OffsetDateTime toOffsetDateTime() {
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(getOffsetSecond());
        return OffsetDateTime.of(getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond(), getNano(), offset);
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

    private int getOffsetMinute() {
        return (extractOffsetId() - 18*60);
    }

    public int getOffsetSecond() {
        return getOffsetMinute() * 60;
    }

    public String toString() {
        char[] buf = new char[32];
        int i = 0;

        i = appendDate(buf, i);

        buf[i++] = 'T';

        i = appendTime(buf, i);

        i = appendOffsetMinute(getOffsetMinute(), buf, i);

        return new String(buf, 0, i);
    }


}
