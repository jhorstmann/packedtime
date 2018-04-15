package net.jhorstmann.packedtime;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public class PackedOffsetDateTime extends AbstractPackedDateTime {

    private PackedOffsetDateTime(long value) {
        super(value);
    }

    public static PackedOffsetDateTime valueOf(long value) {
        return new PackedOffsetDateTime(value);
    }

    public static PackedOffsetDateTime fromOffsetDateTime(OffsetDateTime offsetDateTime) {

        return new PackedOffsetDateTime(encodeWithOffsetSeconds(offsetDateTime.getYear(),
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
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(getOffsetSecond());
        return OffsetDateTime.of(extractYear(), extractMonth(), extractDay(),
                extractHour(), extractMinute(), extractSecond(), extractNano(),
                offset);
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

    public int getOffsetSecond() {
        return extractOffsetSecond();
    }

    public String toString() {
        char[] buf = new char[32];
        int i = 0;

        i = appendDate(buf, i);

        buf[i++] = 'T';

        i = appendTime(buf, i);

        i = appendOffsetMinute(extractOffsetMinute(), buf, i);

        return new String(buf, 0, i);
    }


}
