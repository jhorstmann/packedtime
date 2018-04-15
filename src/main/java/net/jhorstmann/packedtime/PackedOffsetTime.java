package net.jhorstmann.packedtime;

import java.time.OffsetTime;
import java.time.ZoneOffset;

public class PackedOffsetTime extends AbstractPackedDateTime {
    private PackedOffsetTime(long value) {
        super(value);
    }

    public static PackedOffsetTime valueOf(long value) {
        return new PackedOffsetTime(value);
    }

    public static PackedOffsetTime fromOffsetTime(OffsetTime offsetTime) {
        return new PackedOffsetTime(encodeWithOffsetSeconds(0, 0, 0,
                offsetTime.getHour(),
                offsetTime.getMinute(),
                offsetTime.getSecond(),
                offsetTime.getNano(),
                offsetTime.getOffset().getTotalSeconds()));
    }

    public static PackedOffsetTime parse(String str) {
        return DateTimeParser.parseOffsetTime(str);
    }

    public static OffsetTime toOffsetTime(long value) {
        return valueOf(value).toOffsetTime();
    }

    public OffsetTime toOffsetTime() {
        ZoneOffset offset = ZoneOffset.ofTotalSeconds(extractOffsetSecond());
        return OffsetTime.of(extractHour(), extractMinute(), extractSecond(), extractNano(), offset);
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

    private int getOffsetMinute() {
        return extractOffsetMinute();
    }

    public int getOffsetSecond() {
        return extractOffsetSecond();
    }

    public String toString() {
        char[] buf = new char[20];
        int i;

        i = appendTime(buf, 0);

        int offsetMinute = extractOffsetMinute();
        if (offsetMinute == 0) {
            buf[i++] = 'Z';
        } else {
            i = appendOffsetMinute(offsetMinute, buf, i);
        }

        return new String(buf, 0, i);
    }

}
