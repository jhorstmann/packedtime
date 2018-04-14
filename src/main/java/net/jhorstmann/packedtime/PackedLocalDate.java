package net.jhorstmann.packedtime;

import java.time.LocalDate;

public class PackedLocalDate extends AbstractPackedDateTime {
    private PackedLocalDate(long value) {
        super(value);
    }

    public static PackedLocalDate valueOf(long value) {
        return new PackedLocalDate(value);
    }

    public static PackedLocalDate fromLocalDate(LocalDate localDate) {
        return new PackedLocalDate(encode(localDate.getYear(),
                localDate.getMonthValue(),
                localDate.getDayOfMonth(),
                0, 0, 0, 0, 0));
    }

    public static PackedLocalDate parse(String str) {
        return fromLocalDate(LocalDate.parse(str));
    }

    public static LocalDate toLocalDate(long value) {
        return valueOf(value).toLocalDate();
    }

    public LocalDate toLocalDate() {
        return LocalDate.of(extractYear(), extractMonth(), extractDay());
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

    public String toString() {
        char[] buf = new char[16];

        int len = appendDate(buf, 0);

        return new String(buf, 0, len);
    }

}
