package net.jhorstmann.packedtime;

import java.time.LocalTime;

public class PackedLocalTime extends AbstractPackedDateTime {
    PackedLocalTime(long value) {
        super(value);
    }

    public static PackedLocalTime valueOf(long value) {
        return new PackedLocalTime(value);
    }

    public static PackedLocalTime fromLocalTime(LocalTime localTime) {
        return new PackedLocalTime(encode(0, 0, 0,
                localTime.getHour(),
                localTime.getMinute(),
                localTime.getSecond(),
                localTime.getNano(),
                0));
    }

    public static PackedLocalTime parse(String str) {
        return DateTimeParser.parseLocalTime(str);
    }

    public static LocalTime toLocalTime(long value) {
        return valueOf(value).toLocalTime();
    }

    public LocalTime toLocalTime() {
        return LocalTime.of(extractHour(), extractMinute(), extractSecond(), extractNano());
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

    public int getNanos() {
        return extractNano();
    }

    public String toString() {
        byte[] buf = new byte[16];

        int len = appendTime(buf, 0);

        return ascii(buf, len);
    }

}
