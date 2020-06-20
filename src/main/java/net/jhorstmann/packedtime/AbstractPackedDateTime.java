package net.jhorstmann.packedtime;

abstract class AbstractPackedDateTime {
    static final int OFFSET_BITS = 12;
    private static final int MILLI_BITS = 10;
    private static final int SECOND_BITS = 6;
    private static final int MINUTE_BITS = 6;
    private static final int HOUR_BITS = 5;
    private static final int DAY_BITS = 5;
    private static final int MONTH_BITS = 4;
    private static final int YEAR_BITS = 64 - (MONTH_BITS + DAY_BITS + HOUR_BITS + MINUTE_BITS + SECOND_BITS + MILLI_BITS + OFFSET_BITS);

    private static final int MIN_YEAR_INTERNAL = -(1 << (YEAR_BITS - 1));
    private static final int MAX_YEAR_INTERNAL = (1 << (YEAR_BITS - 1)) - 1;
    private static final int MIN_YEAR = -9999;
    private static final int MAX_YEAR = 9999;

    private static final int MIN_OFFSET_MINUTES_INTERNAL = -(1 << (OFFSET_BITS - 1));
    private static final int MAX_OFFSET_MINUTES_INTERNAL = (1 << (OFFSET_BITS - 1)) - 1;

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

    AbstractPackedDateTime(long value) {
        this.value = value;
    }

    static long encode(int year, int month, int day, int hour, int minute, int second, int nano, int offsetId) {
        if (year < MIN_YEAR || year > MAX_YEAR) {
            throw new IllegalStateException("Year is outside of allowed range " + MIN_YEAR + " to " + MAX_YEAR + ": " + year);
        }

        int milli = nano / 1_000_000;

        return ((((((((long) year) << MONTH_BITS
                | (long) month) << DAY_BITS
                | (long) day) << HOUR_BITS
                | (long) hour) << MINUTE_BITS
                | (long) minute) << SECOND_BITS
                | (long) second) << MILLI_BITS
                | (long) milli) << OFFSET_BITS
                | offsetId;
    }

    static long encodeWithOffsetSeconds(int year, int month, int day, int hour, int minute, int second, int nano, int offsetSeconds) {
        if (offsetSeconds % 60 != 0) {
            throw new IllegalStateException("Time zone offset with second precision is not supported: " + offsetSeconds);
        }

        int offsetMinutes = offsetSeconds / 60;

        if (offsetMinutes < MIN_OFFSET_MINUTES || offsetMinutes > MAX_OFFSET_MINUTES) {
            throw new IllegalStateException("Zone offset outside of allowed range " + MIN_OFFSET_HOURS + " to " + MAX_OFFSET_HOURS);
        }

        return encode(year, month, day, hour, minute, second, nano, offsetMinutes + -MIN_OFFSET_MINUTES);
    }

    int extractYear() {
        return (int) ((value >> (MONTH_BITS + DAY_BITS + HOUR_BITS + MINUTE_BITS + SECOND_BITS + MILLI_BITS + OFFSET_BITS)));
    }

    int extractMonth() {
        return (int) ((value >> (DAY_BITS + HOUR_BITS + MINUTE_BITS + SECOND_BITS + MILLI_BITS + OFFSET_BITS)) & ((1 << MONTH_BITS) - 1));
    }

    int extractDay() {
        return (int) ((value >> (HOUR_BITS + MINUTE_BITS + SECOND_BITS + MILLI_BITS + OFFSET_BITS)) & ((1 << DAY_BITS) - 1));
    }

    int extractHour() {
        return (int) ((value >> (MINUTE_BITS + SECOND_BITS + MILLI_BITS + OFFSET_BITS)) & ((1 << HOUR_BITS) - 1));
    }

    int extractMinute() {
        return (int) ((value >> (SECOND_BITS + MILLI_BITS + OFFSET_BITS)) & ((1 << MINUTE_BITS) - 1));
    }

    int extractSecond() {
        return (int) ((value >> (MILLI_BITS + OFFSET_BITS)) & ((1 << SECOND_BITS) - 1));
    }

    int extractMilli() {
        return (int) ((value >> (OFFSET_BITS)) & ((1 << MILLI_BITS) - 1));
    }

    int extractNano() {
        return extractMilli() * 1_000_000;
    }

    int extractOffsetId() {
        return (int) (value & ((1 << OFFSET_BITS) - 1));
    }

    int extractOffsetMinute() {
        return (extractOffsetId() + MIN_OFFSET_MINUTES);
    }

    int extractOffsetSecond() {
        return extractOffsetMinute() * 60;
    }

    public long getValue() {
        return value;
    }

    @Override
    public String toString() {
        return Long.toHexString(value);
    }

    int appendDate(char[] buf, int i) {
        int year = extractYear();
        int month = extractMonth();
        int day = extractDay();

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

    int appendTime(char[] buf, int i) {
        int hour = extractHour();
        int minute = extractMinute();
        int second = extractSecond();
        int milli = extractMilli();

        buf[i++] = (char) ('0' + hour / 10);
        buf[i++] = (char) ('0' + hour % 10);
        buf[i++] = ':';

        buf[i++] = (char) ('0' + minute / 10);
        buf[i++] = (char) ('0' + minute % 10);

        if (second > 0 || milli > 0) {
            buf[i++] = ':';

            buf[i++] = (char) ('0' + second / 10);
            buf[i++] = (char) ('0' + second % 10);

            if (milli > 0) {
                buf[i++] = '.';
                buf[i++] = (char) ('0' + milli / 100 % 10);
                buf[i++] = (char) ('0' + milli / 10 % 10);
                buf[i++] = (char) ('0' + milli % 10);
            }
        }

        return i;
    }

    int appendOffsetMinute(int totalMinutes, char[] buf, int i) {
        if (totalMinutes < 0 || totalMinutes == 0 ) {
            buf[i++] = '-';
            totalMinutes = -totalMinutes;
        } else {
            buf[i++] = '+';
        }
        int offsetHour = totalMinutes / 60;
        int offsetMinute = totalMinutes % 60;

        buf[i++] = (char) ('0' + offsetHour / 10);
        buf[i++] = (char) ('0' + offsetHour % 10);
        buf[i++] = ':';
        buf[i++] = (char) ('0' + offsetMinute / 10);
        buf[i++] = (char) ('0' + offsetMinute % 10);

        return i;
    }

    int appendOffsetSeconds(int totalSeconds, char[] buf, int i) {
        if (totalSeconds < 0) {
            buf[i++] = '-';
            totalSeconds = -totalSeconds;
        } else {
            buf[i++] = '+';
        }

        int totalMinutes = totalSeconds / 60;

        int offsetHour = totalMinutes / 60;
        int offsetMinute = totalMinutes % 60;
        int offsetSecond = totalSeconds % 60;

        buf[i++] = (char) ('0' + offsetHour / 10);
        buf[i++] = (char) ('0' + offsetHour % 10);
        buf[i++] = ':';
        buf[i++] = (char) ('0' + offsetMinute / 10);
        buf[i++] = (char) ('0' + offsetMinute % 10);

        if (offsetSecond != 0) {
            buf[i++] = ':';
            buf[i++] = (char) ('0' + offsetSecond / 10);
            buf[i++] = (char) ('0' + offsetSecond % 10);
        }

        return i;
    }


    @Override
    public final boolean equals(Object other) {
        if (other == null) {
            return false;
        }

        if (other.getClass() != getClass()) {
            return false;
        }

        return ((AbstractPackedDateTime)other).value == value;
    }

    @Override
    public final int hashCode() {
        return Long.hashCode(value);
    }
}
