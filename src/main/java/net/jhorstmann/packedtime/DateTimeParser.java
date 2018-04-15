package net.jhorstmann.packedtime;

import java.time.Year;
import java.time.format.DateTimeParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class DateTimeParser {
    private static final String DATE = "(-?[0-9]{4})-([0-9]{2})-([0-9]{2})";
    private static final String TIME = "([0-9]{2}):([0-9]{2}):([0-9]{2})(?:\\.([0-9]{3}))?";
    private static final String OFFSET = "(Z|[-+][0-9]{2}(?::[0-9]{2}))?";

    private static final Pattern OFFSET_DATE_TIME_PATTERN = Pattern.compile(DATE + "T" + TIME + OFFSET);
    private static final Pattern LOCAL_DATE_TIME_PATTERN = Pattern.compile(DATE + "T" + TIME);
    private static final Pattern LOCAL_DATE_PATTERN = Pattern.compile(DATE);
    private static final Pattern LOCAL_TIME_PATTERN = Pattern.compile(TIME);
    private static final Pattern OFFSET_TIME_PATTERN = Pattern.compile(TIME + OFFSET);

    static PackedOffsetDateTime parseOffsetDateTime(String str) {
        Matcher matcher = OFFSET_DATE_TIME_PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw new DateTimeParseException("Could not parse OffsetDateTime " + str, str, 0);
        }

        int year = parseYear(str, matcher.start(1));
        int month = parse2(str, matcher.start(2));
        int day = parse2(str, matcher.start(3));
        int hour = parse2(str, matcher.start(4));
        int minute = parse2(str, matcher.start(5));
        int second = parse2(str, matcher.start(6));
        int nano = parseOptionalNano(str, matcher.start(7));
        int offsetMinute = parseOffsetMinute(str, matcher.start(8), matcher.end(8));
        int offsetSecond = offsetMinute * 60;

        validateDate(str, matcher, 1, year, month, day);
        validateTime(str, matcher, 4, hour, minute, second);

        long encoded = AbstractPackedDateTime.encodeWithOffsetSeconds(year, month, day, hour, minute, second, nano, offsetSecond);
        return PackedOffsetDateTime.valueOf(encoded);
    }

    static PackedLocalDateTime parseLocalDateTime(String str) {
        Matcher matcher = LOCAL_DATE_TIME_PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw new DateTimeParseException("Could not parse LocalDateTime " + str, str, 0);
        }

        int year = parseYear(str, matcher.start(1));
        int month = parse2(str, matcher.start(2));
        int day = parse2(str, matcher.start(3));
        int hour = parse2(str, matcher.start(4));
        int minute = parse2(str, matcher.start(5));
        int second = parse2(str, matcher.start(6));
        int nano = parseOptionalNano(str, matcher.start(7));

        validateDate(str, matcher, 1, year, month, day);
        validateTime(str, matcher, 4, hour, minute, second);

        long encoded = AbstractPackedDateTime.encode(year, month, day, hour, minute, second, nano, 0);
        return PackedLocalDateTime.valueOf(encoded);
    }

    static PackedLocalDate parseLocalDate(String str) {
        Matcher matcher = LOCAL_DATE_PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw new DateTimeParseException("Could not parse LocalDate " + str, str, 0);
        }

        int year = parseYear(str, matcher.start(1));
        int month = parse2(str, matcher.start(2));
        int day = parse2(str, matcher.start(3));

        validateDate(str, matcher, 1, year, month, day);

        long encoded = AbstractPackedDateTime.encode(year, month, day, 0, 0, 0, 0, 0);
        return PackedLocalDate.valueOf(encoded);
    }

    static PackedLocalTime parseLocalTime(String str) {
        Matcher matcher = LOCAL_TIME_PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw new DateTimeParseException("Could not parse LocalTime " + str, str, 0);
        }

        int hour = parse2(str, matcher.start(1));
        int minute = parse2(str, matcher.start(2));
        int second = parse2(str, matcher.start(3));
        int nano = parseOptionalNano(str, matcher.start(4));

        validateTime(str, matcher, 1, hour, minute, second);

        long encoded = AbstractPackedDateTime.encode(0, 0, 0, hour, minute, second, nano, 0);
        return PackedLocalTime.valueOf(encoded);
    }

    static PackedOffsetTime parseOffsetTime(String str) {
        Matcher matcher = OFFSET_TIME_PATTERN.matcher(str);
        if (!matcher.matches()) {
            throw new DateTimeParseException("Could not parse LocalOffsetTime " + str, str, 0);
        }

        int hour = parse2(str, matcher.start(1));
        int minute = parse2(str, matcher.start(2));
        int second = parse2(str, matcher.start(3));
        int nano = parseOptionalNano(str, matcher.start(4));
        int offsetMinute = parseOffsetMinute(str, matcher.start(5), matcher.end(5));
        int offsetSecond = offsetMinute * 60;

        validateTime(str, matcher, 1, hour, minute, second);

        long encoded = AbstractPackedDateTime.encodeWithOffsetSeconds(0, 0, 0, hour, minute, second, nano, offsetSecond);
        return PackedOffsetTime.valueOf(encoded);
    }

    private static void validateDate(String str, Matcher matcher, int firstGroup, int year, int month, int day) {
        if (month < 1 || month > 12) {
            throw new DateTimeParseException("Month out of range", str, matcher.start(firstGroup+1));
        }

        if (day < 1 || day > 31) {
            throw new DateTimeParseException("Day out of range", str, matcher.start(firstGroup + 2));
        } else {
            switch (month) {
                case 2:
                    if (!Year.isLeap(year) && day > 28 || day > 29) {
                        throw new DateTimeParseException("Day out of range", str, matcher.start(firstGroup + 2));
                    }
                    break;
                case 4:
                case 6:
                case 9:
                case 11:
                    if (day > 30) {
                        throw new DateTimeParseException("Day out of range", str, matcher.start(firstGroup +2));
                    }
                    break;
            }
        }
    }

    private static void validateTime(String str, Matcher matcher, int firstGroup, int hour, int minute, int second) {
        if (hour > 23) {
            throw new DateTimeParseException("Hour out of range", str, matcher.start(firstGroup));
        }
        if (minute > 59) {
            throw new DateTimeParseException("Minute out of range", str, matcher.start(firstGroup+1));
        }
        if (second > 59) {
            throw new DateTimeParseException("Second out of range", str, matcher.start(firstGroup+2));
        }
    }

    private static int parseYear(String str, int start) {
        if (str.charAt(start) == '-') {
            return -parse4(str, start + 1);
        } else {
            return parse4(str, start);
        }
    }

    private static int parseOptionalNano(String str, int start) {
        if (start != -1) {
            return parse3(str, start) * 1_000_000;
        } else {
            return 0;
        }
    }

    private static int parseOffsetMinute(String str, int start, int end) {
        char firstChar = str.charAt(start);
        int offsetSecond;
        if (firstChar == 'Z') {
            offsetSecond = 0;
        } else {
            int offsetHour = parse2(str, start + 1);

            if (offsetHour > 18) {
                throw new DateTimeParseException("Timezone offset out of range", str, start+1);
            }

            if (end - start > 3) {
                int offsetMinute = parse2(str, start + 4);

                if (offsetMinute > 59) {
                    throw new DateTimeParseException("Timezone offset out of range", str, start+4);
                }

                offsetSecond = offsetHour * 60 + offsetMinute;
            } else {
                offsetSecond = offsetHour * 60;
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

    private static int digit(String s, int i) {
        return s.charAt(i) - '0';
    }
}
