package net.jhorstmann.packedtime;

import java.time.LocalDateTime;
import java.time.Year;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;

class DateTimeParser {

    static class Index {
        private int i;

        Index() {
            this.i = 0;
        }

        int get() {
            return i;
        }

        void inc() {
            ++i;
        }

        void inc(int c) {
            i += c;
        }

        int postInc() {
            return i++;
        }

        int postInc(int c) {
            int r = i;
            this.i = r + c;
            return r;
        }
    }

    static class Date {
        int year, month, day;

        public Date(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }
    }

    static class Time {
        int hour, minute, second, nano;

        public Time(int hour, int minute, int second, int nano) {
            this.hour = hour;
            this.minute = minute;
            this.second = second;
            this.nano = nano;
        }
    }

    private static Date parseDate(String str, Index idx) {
        int year, yearStart;

        if (str.charAt(idx.get()) == '-') {
            idx.inc();
            yearStart = 1;
            year = -parseYear(str, idx);
        } else {
            yearStart = 0;
            year = parseYear(str, idx);
        }

        expect(str, idx.postInc(), '-');

        int month = parse2(str, idx.postInc(2));
        expect(str, idx.postInc(), '-');

        int day = parse2(str, idx.postInc(2));

        validateDate(str, yearStart, year, month, day);

        return new Date(year, month, day);
    }

    private static Time parseTime(String str, Index idx) {
        int timeStart = idx.get();

        int hour = parse2(str, idx.postInc(2));
        expect(str, idx.postInc(), ':');

        int minute = parse2(str, idx.postInc(2));

        int second = 0, nano = 0;
        if (idx.get() < str.length()) {
            char ch = str.charAt(idx.get());
            if (ch == '.') {
                idx.inc();
                nano = parseNano(str, idx);
            } else if (ch == ':') {
                idx.inc();
                second = parse2(str, idx.postInc(2));
                if (idx.get() < str.length() && str.charAt(idx.get()) == '.') {
                    idx.inc();
                    nano = parseNano(str, idx);
                }
            }
        }

        validateTime(str, timeStart, hour, minute, second);

        return new Time(hour, minute, second, nano);
    }

    static PackedOffsetDateTime parseOffsetDateTime(String str) {

        Index idx = new Index();

        Date date = parseDate(str, idx);

        expect(str, idx.postInc(), 'T');

        Time time = parseTime(str, idx);

        int offsetMinute = parseOffsetMinute(str, idx);
        int offsetSecond = offsetMinute * 60;

        if (str.length() > idx.get()) {
            throw new DateTimeParseException("trailing characters", str, idx.get());
        }

        long encoded = AbstractPackedDateTime.encodeWithOffsetSeconds(date.year, date.month, date.day, time.hour, time.minute, time.second, time.nano, offsetSecond);
        return PackedOffsetDateTime.valueOf(encoded);
    }

    static PackedOffsetDateTime parseOffsetDateTimeWithDefaultOffset(String str, int defaultOffsetSeconds) {
        Index idx = new Index();

        Date date = parseDate(str, idx);

        expect(str, idx.postInc(), 'T');

        Time time = parseTime(str, idx);

        int offsetSecond;
        if (str.length() > idx.get()) {
            int offsetMinute = parseOffsetMinute(str, idx);
            offsetSecond = offsetMinute * 60;
        } else {
            offsetSecond = defaultOffsetSeconds;
        }

        if (str.length() > idx.get()) {
            throw new DateTimeParseException("trailing characters", str, idx.get());
        }

        long encoded = AbstractPackedDateTime.encodeWithOffsetSeconds(date.year, date.month, date.day, time.hour, time.minute, time.second, time.nano, offsetSecond);
        return PackedOffsetDateTime.valueOf(encoded);
    }

    static PackedOffsetDateTime parseOffsetDateTimeWithDefaultZone(String str, ZoneId zoneId) {
        Index idx = new Index();

        Date date = parseDate(str, idx);

        expect(str, idx.postInc(), 'T');

        Time time = parseTime(str, idx);

        int offsetSecond;
        if (str.length() > idx.get()) {
            int offsetMinute = parseOffsetMinute(str, idx);
            offsetSecond = offsetMinute * 60;
        } else {
            ZoneOffset offset = zoneId.getRules().getOffset(LocalDateTime.of(date.year, date.month, date.day, time.hour, time.minute, time.second, time.nano));
            offsetSecond = offset.getTotalSeconds();
        }

        if (str.length() > idx.get()) {
            throw new DateTimeParseException("trailing characters", str, idx.get());
        }

        long encoded = AbstractPackedDateTime.encodeWithOffsetSeconds(date.year, date.month, date.day, time.hour, time.minute, time.second, time.nano, offsetSecond);
        return PackedOffsetDateTime.valueOf(encoded);
    }

    static PackedLocalDateTime parseLocalDateTime(String str) {
        Index idx = new Index();

        Date date = parseDate(str, idx);

        expect(str, idx.postInc(), 'T');

        Time time = parseTime(str, idx);

        if (str.length() > idx.get()) {
            throw new DateTimeParseException("trailing characters", str, idx.get());
        }

        long encoded = AbstractPackedDateTime.encode(date.year, date.month, date.day, time.hour, time.minute, time.second, time.nano, 0);
        return PackedLocalDateTime.valueOf(encoded);
    }

    static PackedLocalDate parseLocalDate(String str) {
        Index idx = new Index();

        Date date = parseDate(str, idx);

        if (str.length() > idx.get()) {
            throw new DateTimeParseException("trailing characters", str, idx.get());
        }

        long encoded = AbstractPackedDateTime.encode(date.year, date.month, date.day, 0, 0, 0, 0, 0);
        return PackedLocalDate.valueOf(encoded);
    }

    static PackedLocalTime parseLocalTime(String str) {
        Index idx = new Index();

        Time time = parseTime(str, idx);

        if (str.length() > idx.get()) {
            throw new DateTimeParseException("trailing characters", str, idx.get());
        }

        long encoded = AbstractPackedDateTime.encode(0, 0, 0, time.hour, time.minute, time.second, time.nano, 0);
        return PackedLocalTime.valueOf(encoded);
    }

    static PackedOffsetTime parseOffsetTime(String str) {
        Index idx = new Index();

        Time time = parseTime(str, idx);

        int offsetMinute = parseOffsetMinute(str, idx);
        int offsetSecond = offsetMinute * 60;

        long encoded = AbstractPackedDateTime.encodeWithOffsetSeconds(0, 0, 0, time.hour, time.minute, time.second, time.nano, offsetSecond);
        return PackedOffsetTime.valueOf(encoded);
    }

    private static void validateDate(String str, int yearStart, int year, int month, int day) {
        if (month < 1 || month > 12) {
            throw new DateTimeParseException("Month out of range", str, yearStart + 4 + 1);
        }

        if (day < 1 || day > 31) {
            throw new DateTimeParseException("Day out of range", str, yearStart + 9);
        } else {
            switch (month) {
                case 2:
                    if (!Year.isLeap(year) && day > 28 || day > 29) {
                        throw new DateTimeParseException("Day out of range", str, yearStart + 9);
                    }
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    if (day > 30) {
                        throw new DateTimeParseException("Day out of range", str, yearStart + 9);
                    }
                    break;
            }
        }
    }

    private static void validateTime(String str, int timeStart, int hour, int minute, int second) {
        if (hour > 23) {
            throw new DateTimeParseException("Hour out of range", str, timeStart);
        }
        if (minute > 59) {
            throw new DateTimeParseException("Minute out of range", str, timeStart + 3);
        }
        if (second > 59) {
            throw new DateTimeParseException("Second out of range", str, timeStart + 6);
        }
    }

    private static int parseYear(String str, Index idx) {
        return parse4(str, idx.postInc(4));
    }


    private static final int[] NANO_MULTIPLIER = {100_000_000, 10_000_000, 1_000_000};

    private static int parseNano(String str, Index idx) {
        int r = digit(str, idx.postInc());
        int i;

        for (i = 1; i < 3 && idx.get() < str.length(); i++) {
            int ch = str.charAt(idx.get());
            if (ch >= '0' && ch <= '9') {
                r = r * 10 + (ch - '0');
                idx.inc();
            } else {
                return r * NANO_MULTIPLIER[i-1];
            }
        }
        for (; i < 9 && idx.get() < str.length(); i++) {
            int ch = str.charAt(idx.get());
            if (ch >= '0' && ch <= '9') {
                idx.inc();
            } else {
                break;
            }
        }
        return r * NANO_MULTIPLIER[2];
    }

    private static int parseOffsetMinute(String str, Index idx) {
        int start = idx.get();
        char firstChar = str.charAt(start);
        int offsetSecond;
        if (firstChar == 'Z') {
            idx.inc();
            offsetSecond = 0;
            if (str.length() > idx.get()) {
                throw new DateTimeParseException("trailing characters after timezone", str, start);
            }
        } else {
            expect(str, start, '+', '-');
            idx.inc();
            int offsetHour = parse2(str, idx.get());

            if (offsetHour > 18) {
                throw new DateTimeParseException("Timezone offset out of range", str, idx.get());
            }
            idx.inc(2);

            int remaining = str.length() - idx.get();
            if (remaining == 3) {
                expect(str, idx.postInc(), ':');
                int offsetMinute = parse2(str, idx.get());

                if (offsetMinute > 59) {
                    throw new DateTimeParseException("Timezone offset out of range", str, idx.get());
                }
                idx.inc(2);

                offsetSecond = offsetHour * 60 + offsetMinute;
            } else if (remaining == 0) {
                offsetSecond = offsetHour * 60;
            } else {
                throw new DateTimeParseException("invalid timezone offset", str, idx.get());
            }
            if (firstChar == '-') {
                offsetSecond = -offsetSecond;
            }
        }
        return offsetSecond;
    }

    private static int parse2(String str, int start) {
        return digit(str, start) * 10 + digit(str, start + 1);
    }

    private static int parse3(String str, int milliStart) {
        return digit(str, milliStart) * 100 + digit(str, milliStart + 1) * 10 + digit(str, milliStart + 2);
    }

    private static int parse4(String str, int start) {
        return digit(str, start) * 1000 + digit(str, start + 1) * 100 + digit(str, start + 2) * 10 + digit(str, start + 3);
    }

    private static void expect(String s, int i, char ch) {
        if (s.charAt(i) != ch) {
            throw new DateTimeParseException("expected '" + ch + "' at index " + i + " but got '" + s.charAt(i) + "'", s, i);
        }
    }

    private static void expect(String s, int i, char ch1, char ch2) {
        if (s.charAt(i) != ch1 && s.charAt(i) != ch2) {
            throw new DateTimeParseException("expected either '" + ch1 + "' or '" + ch2 + "' at index " + i, s, i);
        }
    }

    private static int digit(String s, int i) {
        int ch = s.charAt(i);
        if (ch >= '0' && ch <= '9') {
            return ch - '0';
        } else {
            throw new DateTimeParseException("not a digit at index " + i, s, i);
        }
    }
}
