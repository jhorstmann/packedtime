package net.jhorstmann.packedtime;

import java.time.LocalDateTime;

public class PackedLocalDateTime extends AbstractPackedDateTime {
    private PackedLocalDateTime(long value) {
        super(value);
    }

    public static PackedLocalDateTime valueOf(long value) {
        return new PackedLocalDateTime(value);
    }

    public static PackedLocalDateTime fromLocalDateTime(LocalDateTime localDateTime) {
        return new PackedLocalDateTime(encode(localDateTime.getYear(),
                localDateTime.getMonthValue(),
                localDateTime.getDayOfMonth(),
                localDateTime.getHour(),
                localDateTime.getMinute(),
                localDateTime.getSecond(),
                localDateTime.getNano(),
                0));
    }

    public static PackedLocalDateTime parse(String str) {
        return DateTimeParser.parseLocalDateTime(str);
    }

    public static LocalDateTime toLocalDateTime(long value) {
        return valueOf(value).toLocalDateTime();
    }

    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.of(extractYear(), extractMonth(), extractDay(),
                extractHour(), extractMinute(), extractSecond(), extractNano());
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

        return new String(buf, 0, i);
    }


}
