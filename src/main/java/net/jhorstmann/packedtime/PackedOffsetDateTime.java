package net.jhorstmann.packedtime;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class PackedOffsetDateTime {
    private static final int OFFSET_MINUTES_BITS = 12;
    private static final int MILLI_BITS = 10;
    private static final int SECOND_BITS = 6;
    private static final int MINUTE_BITS = 6;
    private static final int HOUR_BITS = 5;
    private static final int DAY_BITS = 5;
    private static final int MONTH_BITS = 4;
    private static final int YEAR_BITS = 64 - (MONTH_BITS + DAY_BITS + HOUR_BITS + MINUTE_BITS + SECOND_BITS + MILLI_BITS + OFFSET_MINUTES_BITS);

    private static final int MIN_YEAR_INTERNAL = -(1 << (YEAR_BITS - 1));
    private static final int MAX_YEAR_INTERNAL = (1 << (YEAR_BITS - 1)) - 1;
    private static final int MIN_OFFSET_MINUTES_INTERNAL = -(1 << (OFFSET_MINUTES_BITS - 1));
    private static final int MAX_OFFSET_MINUTES_INTERNAL = (1 << (OFFSET_MINUTES_BITS - 1)) - 1;

    private static final int MIN_YEAR = -9999;
    private static final int MAX_YEAR = 9999;
    private static final int MAX_OFFSET_HOURS = 18;
    private static final int MIN_OFFSET_HOURS = -18;
    private static final int MIN_OFFSET_MINUTES = MIN_OFFSET_HOURS * 60;
    private static final int MAX_OFFSET_MINUTES = MAX_OFFSET_HOURS * 60;

    static {
        if (!(MIN_YEAR_INTERNAL < MIN_YEAR || MAX_YEAR_INTERNAL > MAX_YEAR)) {
            throw new AssertionError("Insufficient bits to store year range");
        }
        if (!(MIN_OFFSET_MINUTES_INTERNAL < MIN_OFFSET_MINUTES || MAX_OFFSET_MINUTES_INTERNAL > MAX_OFFSET_MINUTES)) {
            throw new AssertionError("Insufficient bits to store offset range");
        }
    }

    private final long value;

    private PackedOffsetDateTime(long value) {
        this.value = value;
    }

    private static long encode(int year, int month, int day, int hour, int minute, int second, int nano, int offsetSeconds) {
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new IllegalStateException("Year is outside of allowed range " + MIN_YEAR + " to " + MAX_YEAR);
        }

        if (offsetSeconds % 60 != 0) {
            throw new IllegalStateException("Time zone offset with second precision is not supported");
        }

        int offsetMinutes = offsetSeconds / 60;

        if (offsetMinutes < MIN_OFFSET_MINUTES || offsetMinutes > MAX_OFFSET_MINUTES) {
            throw new IllegalStateException("Zone offset outside of allowed range " + MIN_OFFSET_HOURS + " to " + MAX_OFFSET_HOURS);
        }

        int milli = nano / 1_000_000;

        return ((((((((long) year) << MONTH_BITS
                | (long) month) << DAY_BITS
                | (long) day) << HOUR_BITS
                | (long) hour) << MINUTE_BITS
                | (long) minute) << SECOND_BITS
                | (long) second) << MILLI_BITS
                | (long) milli) << OFFSET_MINUTES_BITS
                | (offsetMinutes + 18 * 60);
    }

    public static PackedOffsetDateTime valueOf(long value) {
        return new PackedOffsetDateTime(value);
    }

    public static PackedOffsetDateTime fromOffsetDateTime(OffsetDateTime offsetDateTime) {
        return new PackedOffsetDateTime(encode(offsetDateTime.getYear(),
                offsetDateTime.getMonthValue(),
                offsetDateTime.getDayOfMonth(),
                offsetDateTime.getHour(),
                offsetDateTime.getMinute(),
                offsetDateTime.getSecond(),
                offsetDateTime.getNano(),
                offsetDateTime.getOffset().getTotalSeconds()));
    }

    public static PackedOffsetDateTime parse(String str) {
        return fromOffsetDateTime(OffsetDateTime.parse(str));
    }

    public static OffsetDateTime toOffsetDateTime(long value) {
        return valueOf(value).toOffsetDateTime();
    }

    public OffsetDateTime toOffsetDateTime() {
        return OffsetDateTime.of(getYear(), getMonth(), getDay(), getHour(), getMinute(), getSecond(), getNanos(), ZoneOffset.ofTotalSeconds(getOffsetSecond()));
    }

    public int getYear() {
        return (int) ((value >> (MONTH_BITS + DAY_BITS + HOUR_BITS + MINUTE_BITS + SECOND_BITS + MILLI_BITS + OFFSET_MINUTES_BITS)));
    }

    public int getMonth() {
        return (int) ((value >> (DAY_BITS + HOUR_BITS + MINUTE_BITS + SECOND_BITS + MILLI_BITS + OFFSET_MINUTES_BITS)) & ((1 << MONTH_BITS) - 1));
    }

    public int getDay() {
        return (int) ((value >> (HOUR_BITS + MINUTE_BITS + SECOND_BITS + MILLI_BITS + OFFSET_MINUTES_BITS)) & ((1 << DAY_BITS) - 1));
    }

    public int getHour() {
        return (int) ((value >> (MINUTE_BITS + SECOND_BITS + MILLI_BITS + OFFSET_MINUTES_BITS)) & ((1 << HOUR_BITS) - 1));
    }

    public int getMinute() {
        return (int) ((value >> (SECOND_BITS + MILLI_BITS + OFFSET_MINUTES_BITS)) & ((1 << MINUTE_BITS) - 1));
    }

    public int getSecond() {
        return (int) ((value >> (MILLI_BITS + OFFSET_MINUTES_BITS)) & ((1 << SECOND_BITS) - 1));
    }

    public int getMilli() {
        return (int) ((value >> (OFFSET_MINUTES_BITS)) & ((1 << MILLI_BITS) - 1));
    }

    public int getNanos() {
        return getMilli() * 1_000_000;
    }

    public int getOffsetMinute() {
        return (int) (value & ((1 << OFFSET_MINUTES_BITS) - 1)) - 18 * 60;
    }

    public int getOffsetSecond() {
        return getOffsetMinute() * 60;
    }

    public long getValue() {
        return value;
    }

    public String toString() {
        char[] buf = new char[32];
        int i = 0;

        i = appendDate(buf, i);

        buf[i++] = 'T';

        i = appendTime(buf, i);

        i = appendOffset(buf, i);

        return new String(buf, 0, i);
    }

    private int appendDate(char[] buf, int i) {
        int year = getYear();
        int month = getMonth();
        int day = getDay();

        if (year < 0) {
            buf[i++] = '-';
            year = -year;
        }

        buf[i++] = (char) ('0' + year / 1000);
        buf[i++] = (char) ('0' + year / 100 % 10);
        buf[i++] = (char) ('0' + year / 10 % 10);
        buf[i++] = (char) ('0' + year % 10);
        buf[i++] = '-';

        buf[i++] = (char) ('0' + month / 10);
        buf[i++] = (char) ('0' + month % 10);
        buf[i++] = '-';

        buf[i++] = (char) ('0' + day / 10);
        buf[i++] = (char) ('0' + day % 10);
        return i;
    }

    private int appendTime(char[] buf, int i) {
        int hour = getHour();
        int minute = getMinute();
        int second = getSecond();
        int milli = getMilli();


        buf[i++] = (char) ('0' + hour / 10);
        buf[i++] = (char) ('0' + hour % 10);
        buf[i++] = ':';

        buf[i++] = (char) ('0' + minute / 10);
        buf[i++] = (char) ('0' + minute % 10);

        if (second > 0 || milli > 0) {
            buf[i++] = ':';

            buf[i++] = (char) ('0' + second / 10);
            buf[i++] = (char) ('0' + second % 10);
        }

        if (milli > 0) {
            buf[i++] = '.';
            buf[i++] = (char) ('0' + milli / 100 % 10);
            buf[i++] = (char) ('0' + milli / 10 % 10);
            buf[i++] = (char) ('0' + milli % 10);
        }
        return i;
    }

    private int appendOffset(char[] buf, int i) {
        int offsetMinute = getOffsetMinute();

        if (offsetMinute == 0) {
            buf[i++] = 'Z';
        } else {
            if (offsetMinute > 0) {
                buf[i++] = '+';
            } else {
                buf[i++] = '-';
                offsetMinute = -offsetMinute;
            }
            int offsetHour = offsetMinute / 60;
            offsetMinute %= 60;
            buf[i++] = (char) ('0' + offsetHour / 10);
            buf[i++] = (char) ('0' + offsetHour % 10);
            buf[i++] = ':';
            buf[i++] = (char) ('0' + offsetMinute / 10);
            buf[i++] = (char) ('0' + offsetMinute % 10);
        }
        return i;
    }
}
